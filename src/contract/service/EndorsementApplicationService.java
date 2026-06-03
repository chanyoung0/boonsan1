package contract.service;

import contract.dto.EndorsementCreateRequest;
import contract.dto.EndorsementResponse;
import contract.dto.UnderwritingRequestCompleteRequest;
import contract.dto.UnderwritingRequestCreateRequest;
import contract.dto.UnderwritingRequestResponse;
import contract.mapper.ContractMapper;
import contract.mapper.EndorsementMapper;
import enums.ChangeReason;
import enums.EndorsementStatus;
import enums.PaymentCycle;
import enums.RequestReason;
import enums.RequestStatus;
import enums.UnderwritingResultType;
import model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EndorsementApplicationService {

    private final ContractApplicationService contractApplicationService;
    private final UnderwritingRequestApplicationService underwritingRequestApplicationService;
    private final ContractMapper contractMapper;
    private final EndorsementMapper endorsementMapper;

    public EndorsementApplicationService(
            ContractApplicationService contractApplicationService,
            UnderwritingRequestApplicationService underwritingRequestApplicationService,
            ContractMapper contractMapper,
            EndorsementMapper endorsementMapper
    ) {
        this.contractApplicationService = contractApplicationService;
        this.underwritingRequestApplicationService = underwritingRequestApplicationService;
        this.contractMapper = contractMapper;
        this.endorsementMapper = endorsementMapper;
    }

    @Transactional
    public EndorsementResponse apply(String policyNumber, EndorsementCreateRequest request) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        EndorsementResponse active = endorsementMapper.findActiveByPolicyNumber(contract.getPolicyNumber());
        if (active != null) {
            throw new IllegalArgumentException(
                    "Active endorsement already exists: " + active.getEndorsementId());
        }
        if (request.getEndorsementType() == null) {
            throw new IllegalArgumentException("endorsementType is required.");
        }
        if (request.getChangeReason() == null) {
            throw new IllegalArgumentException("changeReason is required.");
        }
        requireText(request.getPreviousContent(), "previousContent");
        requireText(request.getNewContent(), "newContent");

        String endorsementId = generateEndorsementId();
        LocalDateTime appliedAt = LocalDateTime.now();

        endorsementMapper.insertEndorsement(
                endorsementId,
                contract.getPolicyNumber(),
                request.getEndorsementType().name(),
                request.getChangeReason().name(),
                request.getPreviousContent().trim(),
                request.getNewContent().trim(),
                EndorsementStatus.APPLIED.name(),
                appliedAt
        );
        return endorsementMapper.findById(endorsementId);
    }

    @Transactional(readOnly = true)
    public EndorsementResponse getActive(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        EndorsementResponse active = endorsementMapper.findActiveByPolicyNumber(contract.getPolicyNumber());
        if (active == null) {
            throw new NoSuchElementException("No active endorsement for contract: " + contract.getPolicyNumber());
        }
        return active;
    }

    @Transactional(readOnly = true)
    public List<EndorsementResponse> listByPolicyNumber(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        return endorsementMapper.findByPolicyNumber(contract.getPolicyNumber());
    }

    @Transactional
    public UnderwritingRequestResponse requestUnderwriting(
            String policyNumber,
            UnderwritingRequestCreateRequest request
    ) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        EndorsementResponse active = requireActive(contract.getPolicyNumber());
        if (active.getUnderwritingRequestId() != null) {
            throw new IllegalArgumentException(
                    "Underwriting request already exists for endorsement: " + active.getEndorsementId());
        }
        if (!requiresUnderwriting(active.getChangeReason())) {
            throw new IllegalArgumentException(
                    "This endorsement does not require underwriting: " + active.getChangeReason());
        }

        UnderwritingRequestResponse uw = underwritingRequestApplicationService.createForSource(
                contract.getPolicyNumber(),
                RequestReason.ENDORSEMENT,
                active.getEndorsementId(),
                request == null ? null : request.getUnderwritingType()
        );
        int updated = endorsementMapper.updateUnderwritingRequestId(
                active.getEndorsementId(),
                uw.getRequestId()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Link underwriting request failed (concurrent modification): " + active.getEndorsementId());
        }
        return uw;
    }

    @Transactional
    public UnderwritingRequestResponse completeUnderwriting(
            String policyNumber,
            UnderwritingRequestCompleteRequest request
    ) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        EndorsementResponse active = requireActive(contract.getPolicyNumber());
        if (active.getUnderwritingRequestId() == null) {
            throw new IllegalArgumentException(
                    "No underwriting request linked to endorsement: " + active.getEndorsementId());
        }
        return underwritingRequestApplicationService.complete(active.getUnderwritingRequestId(), request);
    }

    @Transactional
    public EndorsementResponse approve(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        EndorsementResponse active = requireActive(contract.getPolicyNumber());
        if (requiresUnderwriting(active.getChangeReason())) {
            requireApprovedOrSurchargeUnderwriting(active);
        }

        applyContractChange(contract, active);

        int updated = endorsementMapper.updateStatusToApproved(
                active.getEndorsementId(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Endorsement approve failed (concurrent modification): " + active.getEndorsementId());
        }
        return endorsementMapper.findById(active.getEndorsementId());
    }

    @Transactional
    public EndorsementResponse reject(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        EndorsementResponse active = requireActive(contract.getPolicyNumber());
        requireUnderwritingResult(active, UnderwritingResultType.REJECTED, "reject");

        int updated = endorsementMapper.updateStatusToRejected(
                active.getEndorsementId(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Endorsement reject failed (concurrent modification): " + active.getEndorsementId());
        }
        return endorsementMapper.findById(active.getEndorsementId());
    }

    @Transactional
    public EndorsementResponse cancel(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        EndorsementResponse active = requireActive(contract.getPolicyNumber());

        int updated = endorsementMapper.updateStatusToCancelled(
                active.getEndorsementId(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Endorsement cancel failed (concurrent modification): " + active.getEndorsementId());
        }
        return endorsementMapper.findById(active.getEndorsementId());
    }

    private void requireUnderwritingResult(
            EndorsementResponse endorsement,
            UnderwritingResultType expected,
            String action
    ) {
        if (endorsement.getUnderwritingRequestId() == null) {
            throw new IllegalArgumentException(
                    "Cannot " + action + " endorsement without completed underwriting request.");
        }
        UnderwritingRequestResponse uw = underwritingRequestApplicationService
                .requireById(endorsement.getUnderwritingRequestId());
        if (uw.getRequestStatus() != RequestStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Underwriting request must be COMPLETED to " + action + ". Current: " + uw.getRequestStatus());
        }
        if (uw.getUnderwritingResult() != expected) {
            throw new IllegalArgumentException(
                    "Underwriting result must be " + expected + " to " + action + ". Current: "
                            + uw.getUnderwritingResult());
        }
    }

    private void requireApprovedOrSurchargeUnderwriting(EndorsementResponse endorsement) {
        if (endorsement.getUnderwritingRequestId() == null) {
            throw new IllegalArgumentException(
                    "Cannot approve endorsement without completed underwriting request.");
        }
        UnderwritingRequestResponse uw = underwritingRequestApplicationService
                .requireById(endorsement.getUnderwritingRequestId());
        if (uw.getRequestStatus() != RequestStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Underwriting request must be COMPLETED to approve. Current: " + uw.getRequestStatus());
        }
        if (uw.getUnderwritingResult() != UnderwritingResultType.APPROVED
                && uw.getUnderwritingResult() != UnderwritingResultType.SURCHARGE) {
            throw new IllegalArgumentException(
                    "Underwriting result must be APPROVED or SURCHARGE to approve. Current: "
                            + uw.getUnderwritingResult());
        }
    }

    private boolean requiresUnderwriting(ChangeReason changeReason) {
        return changeReason == ChangeReason.INSURED_AMOUNT_CHANGE
                || changeReason == ChangeReason.SPECIAL_CONTRACT_ADD
                || changeReason == ChangeReason.SPECIAL_CONTRACT_REMOVE;
    }

    private void applyContractChange(Contract contract, EndorsementResponse endorsement) {
        int updated;
        switch (endorsement.getChangeReason()) {
            case INSURED_AMOUNT_CHANGE:
                BigDecimal insuredAmount = parsePositiveAmount(endorsement.getNewContent(), "insuredAmount");
                updated = contractMapper.updateInsuredAmount(contract.getPolicyNumber(), insuredAmount);
                break;
            case PAYMENT_CYCLE_CHANGE:
                PaymentCycle paymentCycle = parsePaymentCycle(endorsement.getNewContent());
                updated = contractMapper.updatePaymentCycle(contract.getPolicyNumber(), paymentCycle.name());
                break;
            case SPECIAL_CONTRACT_ADD:
                String addedList = updateSpecialContractList(
                        contract.getSpecialContractList(),
                        endorsement.getNewContent(),
                        true
                );
                updated = contractMapper.updateSpecialContractList(contract.getPolicyNumber(), addedList);
                break;
            case SPECIAL_CONTRACT_REMOVE:
                String removedList = updateSpecialContractList(
                        contract.getSpecialContractList(),
                        endorsement.getNewContent(),
                        false
                );
                updated = contractMapper.updateSpecialContractList(contract.getPolicyNumber(), removedList);
                break;
            case BENEFICIARY_CHANGE:
                // The current contract schema has no beneficiary column. The confirmed change remains in endorsement history.
                return;
            default:
                throw new IllegalArgumentException(
                        "Unsupported endorsement change reason: " + endorsement.getChangeReason());
        }
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Contract update failed for endorsement: " + endorsement.getEndorsementId());
        }
    }

    private BigDecimal parsePositiveAmount(String value, String fieldName) {
        try {
            BigDecimal amount = new BigDecimal(value.replace(",", "").trim());
            if (amount.signum() <= 0) {
                throw new IllegalArgumentException(fieldName + " must be greater than 0.");
            }
            return amount;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }

    private PaymentCycle parsePaymentCycle(String value) {
        try {
            return PaymentCycle.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported payment cycle: " + value);
        }
    }

    private String updateSpecialContractList(String currentValue, String requestedValue, boolean add) {
        Set<String> items = new LinkedHashSet<>(splitItems(currentValue));
        List<String> requestedItems = splitItems(requestedValue);
        if (requestedItems.isEmpty()) {
            throw new IllegalArgumentException("special contract item is required.");
        }
        if (add) {
            items.addAll(requestedItems);
        } else {
            items.removeAll(requestedItems);
        }
        return items.isEmpty() ? null : String.join(", ", items);
    }

    private List<String> splitItems(String value) {
        List<String> items = new ArrayList<>();
        if (value == null || value.trim().isEmpty()) {
            return items;
        }
        for (String item : value.split("[,\\n]")) {
            String normalized = item.trim();
            if (!normalized.isEmpty()) {
                items.add(normalized);
            }
        }
        return items;
    }

    private EndorsementResponse requireActive(String policyNumber) {
        EndorsementResponse active = endorsementMapper.findActiveByPolicyNumber(policyNumber);
        if (active == null) {
            throw new NoSuchElementException("No active endorsement for contract: " + policyNumber);
        }
        return active;
    }

    private String generateEndorsementId() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "END-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }
}
