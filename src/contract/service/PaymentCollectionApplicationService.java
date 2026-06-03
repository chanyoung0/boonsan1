package contract.service;

import contract.dto.PaymentCollectionCreateRequest;
import contract.dto.PaymentCollectionBatchRequest;
import contract.dto.PaymentCollectionBatchResponse;
import contract.dto.PaymentCollectionResponse;
import contract.dto.PaymentCollectionTargetResponse;
import contract.dto.PaymentCollectionTransferRequest;
import contract.dto.PaymentCollectionTransferTargetResponse;
import contract.dto.UnpaidNoticeResponse;
import contract.mapper.PaymentCollectionMapper;
import enums.PaymentMethod;
import enums.ProcessingResult;
import enums.TransferType;
import model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentCollectionApplicationService {

    // 연체료 = 미납금액 × 0.05 (insurance-system-architecture 스킬 명시)
    private static final BigDecimal LATE_FEE_RATE = new BigDecimal("0.05");
    private static final String DEFAULT_DELIVERY_METHOD = "SMS";

    private final ContractApplicationService contractApplicationService;
    private final PaymentCollectionMapper paymentCollectionMapper;

    public PaymentCollectionApplicationService(
            ContractApplicationService contractApplicationService,
            PaymentCollectionMapper paymentCollectionMapper
    ) {
        this.contractApplicationService = contractApplicationService;
        this.paymentCollectionMapper = paymentCollectionMapper;
    }

    @Transactional
    public PaymentCollectionResponse createCollection(String policyNumber, PaymentCollectionCreateRequest request) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        if (request.getInstallmentNo() == null || request.getInstallmentNo() < 1) {
            throw new IllegalArgumentException("installmentNo must be >= 1.");
        }
        if (request.getDueDate() == null) {
            throw new IllegalArgumentException("dueDate is required.");
        }
        if (request.getPlannedAmount() == null || request.getPlannedAmount().signum() <= 0) {
            throw new IllegalArgumentException("plannedAmount must be greater than 0.");
        }
        if (request.getCollectedAmount() == null || request.getCollectedAmount().signum() < 0) {
            throw new IllegalArgumentException("collectedAmount must be >= 0.");
        }
        if (request.getPaymentMethod() == null) {
            throw new IllegalArgumentException("paymentMethod is required.");
        }

        BigDecimal unpaidAmount = request.getPlannedAmount().subtract(request.getCollectedAmount());
        if (unpaidAmount.signum() < 0) {
            unpaidAmount = BigDecimal.ZERO;
        }
        BigDecimal lateFee = unpaidAmount.signum() > 0
                ? unpaidAmount.multiply(LATE_FEE_RATE).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2);
        ProcessingResult processingResult = unpaidAmount.signum() > 0
                ? ProcessingResult.FAILED
                : ProcessingResult.SUCCESS;

        LocalDateTime now = LocalDateTime.now();
        String collectionId = generateCollectionId();

        paymentCollectionMapper.insertCollection(
                collectionId,
                contract.getPolicyNumber(),
                request.getInstallmentNo(),
                request.getDueDate(),
                request.getPlannedAmount(),
                request.getCollectedAmount(),
                unpaidAmount,
                lateFee,
                request.getPaymentMethod().name(),
                processingResult.name(),
                now,
                now
        );
        refreshContractUnpaidStatus(contract.getPolicyNumber());

        return requireCollectionOwnedBy(collectionId, contract.getPolicyNumber());
    }

    @Transactional(readOnly = true)
    public List<PaymentCollectionResponse> listByPolicyNumber(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        return paymentCollectionMapper.findByPolicyNumber(contract.getPolicyNumber());
    }

    @Transactional(readOnly = true)
    public List<PaymentCollectionTargetResponse> listCollectionTargets() {
        List<PaymentCollectionTargetResponse> targets = paymentCollectionMapper.findCollectionTargets();
        targets.forEach(target -> target.setAccountNumber(maskAccountNumber(target.getAccountNumber())));
        return targets;
    }

    @Transactional
    public PaymentCollectionBatchResponse processBatch(PaymentCollectionBatchRequest request) {
        List<PaymentCollectionTargetResponse> targets = paymentCollectionMapper.findCollectionTargets();
        Set<String> selectedPolicyNumbers = normalizePolicyNumbers(request == null ? null : request.getPolicyNumbers());
        if (!selectedPolicyNumbers.isEmpty()) {
            targets = targets.stream()
                    .filter(target -> selectedPolicyNumbers.contains(target.getPolicyNumber()))
                    .toList();
        }
        if (targets.isEmpty()) {
            throw new IllegalArgumentException("No due payment collection targets were found.");
        }

        List<PaymentCollectionResponse> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        BigDecimal totalCollectedAmount = BigDecimal.ZERO;

        for (PaymentCollectionTargetResponse target : targets) {
            Contract contract = contractApplicationService.requireContract(target.getPolicyNumber());
            boolean success = isDeterministicAutoTransferSuccess(contract);
            BigDecimal collectedAmount = success ? target.getPlannedAmount() : BigDecimal.ZERO;
            BigDecimal unpaidAmount = success ? BigDecimal.ZERO : target.getPlannedAmount();
            BigDecimal lateFee = unpaidAmount.signum() > 0
                    ? unpaidAmount.multiply(LATE_FEE_RATE).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO.setScale(2);
            ProcessingResult result = success ? ProcessingResult.SUCCESS : ProcessingResult.FAILED;
            LocalDateTime now = LocalDateTime.now();
            String collectionId = generateCollectionId();

            paymentCollectionMapper.insertCollection(
                    collectionId,
                    target.getPolicyNumber(),
                    target.getInstallmentNo(),
                    target.getDueDate(),
                    target.getPlannedAmount(),
                    collectedAmount,
                    unpaidAmount,
                    lateFee,
                    PaymentMethod.AUTO_TRANSFER.name(),
                    result.name(),
                    now,
                    now
            );
            refreshContractUnpaidStatus(target.getPolicyNumber());
            results.add(requireCollectionOwnedBy(collectionId, target.getPolicyNumber()));

            if (success) {
                successCount++;
                totalCollectedAmount = totalCollectedAmount.add(collectedAmount);
            } else {
                failureCount++;
            }
        }

        return new PaymentCollectionBatchResponse(
                targets.size(),
                successCount,
                failureCount,
                totalCollectedAmount,
                results
        );
    }

    @Transactional(readOnly = true)
    public List<PaymentCollectionTransferTargetResponse> listTransferTargets() {
        return paymentCollectionMapper.findTransferTargets();
    }

    @Transactional(readOnly = true)
    public PaymentCollectionResponse findByCollectionId(String policyNumber, String collectionId) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        return requireCollectionOwnedBy(collectionId, contract.getPolicyNumber());
    }

    @Transactional(readOnly = true)
    public UnpaidNoticeResponse getUnpaidNotice(String policyNumber, String collectionId) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        PaymentCollectionResponse collection = requireCollectionOwnedBy(collectionId, contract.getPolicyNumber());
        if (collection.getProcessingResult() != ProcessingResult.FAILED) {
            throw new IllegalArgumentException(
                    "Unpaid notice is only available for FAILED collections. Current: " + collection.getProcessingResult());
        }

        long daysOverdue = ChronoUnit.DAYS.between(collection.getDueDate(), LocalDate.now());
        BigDecimal totalDue = collection.getUnpaidAmount().add(collection.getLateFee());
        String message = composeNoticeMessage(
                collection.getInstallmentNo(),
                collection.getDueDate(),
                daysOverdue,
                collection.getUnpaidAmount(),
                collection.getLateFee(),
                totalDue
        );

        return new UnpaidNoticeResponse(
                collection.getCollectionId(),
                contract.getPolicyNumber(),
                contract.getInsuredName(),
                contract.getInsuredContact(),
                collection.getInstallmentNo(),
                collection.getDueDate(),
                Math.max(0, daysOverdue),
                collection.getUnpaidAmount(),
                collection.getLateFee(),
                totalDue,
                collection.getPaymentMethod(),
                collection.getProcessingResult(),
                message,
                DEFAULT_DELIVERY_METHOD
        );
    }

    @Transactional
    public PaymentCollectionResponse transfer(
            String policyNumber,
            String collectionId,
            PaymentCollectionTransferRequest request
    ) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        PaymentCollectionResponse existing = requireCollectionOwnedBy(collectionId, contract.getPolicyNumber());
        if (existing.getProcessingResult() != ProcessingResult.FAILED) {
            throw new IllegalArgumentException(
                    "Only FAILED collections can be transferred. Current: " + existing.getProcessingResult());
        }
        if (existing.getTransferType() != null) {
            throw new IllegalArgumentException("Collection is already transferred: " + collectionId);
        }
        TransferType transferType = request.getTransferType();
        if (transferType == null) {
            throw new IllegalArgumentException("transferType is required.");
        }

        int updated = paymentCollectionMapper.updateTransfer(
                collectionId,
                transferType.name(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Transfer failed (concurrent modification): " + collectionId);
        }
        return requireCollectionOwnedBy(collectionId, contract.getPolicyNumber());
    }

    private PaymentCollectionResponse requireCollectionOwnedBy(String collectionId, String policyNumber) {
        String normalized = requireText(collectionId, "collectionId");
        PaymentCollectionResponse collection = paymentCollectionMapper.findByCollectionId(normalized);
        if (collection == null) {
            throw new NoSuchElementException("Payment collection not found: " + normalized);
        }
        if (!policyNumber.equals(collection.getPolicyNumber())) {
            throw new IllegalArgumentException(
                    "Collection " + normalized + " does not belong to contract " + policyNumber);
        }
        return collection;
    }

    private void refreshContractUnpaidStatus(String policyNumber) {
        boolean hasUnpaidPremium = paymentCollectionMapper.countLatestFailedInstallments(policyNumber) > 0;
        paymentCollectionMapper.updateContractUnpaidStatus(policyNumber, hasUnpaidPremium);
    }

    private boolean isDeterministicAutoTransferSuccess(Contract contract) {
        String key = contract.getAccountNumber();
        if (key == null || key.isBlank()) {
            key = contract.getPolicyNumber();
        }
        for (int index = key.length() - 1; index >= 0; index--) {
            char character = key.charAt(index);
            if (Character.isDigit(character)) {
                return character != '0' && character != '4' && character != '8';
            }
        }
        return Math.floorMod(key.hashCode(), 5) != 0;
    }

    private Set<String> normalizePolicyNumbers(List<String> policyNumbers) {
        Set<String> normalized = new HashSet<>();
        if (policyNumbers == null) {
            return normalized;
        }
        for (String policyNumber : policyNumbers) {
            if (policyNumber != null && !policyNumber.trim().isEmpty()) {
                normalized.add(policyNumber.trim());
            }
        }
        return normalized;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 5) {
            return accountNumber;
        }
        int visible = 4;
        StringBuilder masked = new StringBuilder();
        for (int index = 0; index < accountNumber.length() - visible; index++) {
            char character = accountNumber.charAt(index);
            masked.append(character == '-' ? '-' : '*');
        }
        masked.append(accountNumber.substring(accountNumber.length() - visible));
        return masked.toString();
    }

    private String composeNoticeMessage(
            int installmentNo,
            LocalDate dueDate,
            long daysOverdue,
            BigDecimal unpaidAmount,
            BigDecimal lateFee,
            BigDecimal totalDue
    ) {
        String unpaidStr = unpaidAmount.toPlainString();
        String lateStr = lateFee.toPlainString();
        String totalStr = totalDue.toPlainString();
        if (daysOverdue <= 0) {
            return installmentNo + "회차(납기 " + dueDate + ") 보험료 " + unpaidStr
                    + "원이 미수금 처리되었습니다. 연체료 " + lateStr + "원 포함 총 " + totalStr + "원을 납부해 주세요.";
        }
        return installmentNo + "회차(납기 " + dueDate + ") 보험료가 " + daysOverdue
                + "일 경과되었습니다. 미납금 " + unpaidStr + "원 + 연체료 " + lateStr
                + "원 = 총 " + totalStr + "원을 즉시 납부해 주세요.";
    }

    private String generateCollectionId() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "COL-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }
}
