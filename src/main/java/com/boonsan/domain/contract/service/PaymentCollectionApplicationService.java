package com.boonsan.domain.contract.service;

import com.boonsan.domain.contract.dto.PaymentCollectionCreateRequest;
import com.boonsan.domain.contract.dto.PaymentCollectionResponse;
import com.boonsan.domain.contract.dto.PaymentCollectionTransferRequest;
import com.boonsan.domain.contract.dto.UnpaidNoticeResponse;
import com.boonsan.domain.contract.mapper.PaymentCollectionMapper;
import com.boonsan.domain.enums.ProcessingResult;
import com.boonsan.domain.enums.TransferType;
import com.boonsan.domain.model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
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

        return requireCollectionOwnedBy(collectionId, contract.getPolicyNumber());
    }

    @Transactional(readOnly = true)
    public List<PaymentCollectionResponse> listByPolicyNumber(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        return paymentCollectionMapper.findByPolicyNumber(contract.getPolicyNumber());
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
