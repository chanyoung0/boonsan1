package com.boonsan.domain.contract.service;

import com.boonsan.domain.contract.dto.EndorsementCreateRequest;
import com.boonsan.domain.contract.dto.EndorsementResponse;
import com.boonsan.domain.contract.dto.UnderwritingRequestCompleteRequest;
import com.boonsan.domain.contract.dto.UnderwritingRequestCreateRequest;
import com.boonsan.domain.contract.dto.UnderwritingRequestResponse;
import com.boonsan.domain.contract.mapper.EndorsementMapper;
import com.boonsan.domain.enums.EndorsementStatus;
import com.boonsan.domain.enums.RequestReason;
import com.boonsan.domain.enums.RequestStatus;
import com.boonsan.domain.enums.UnderwritingResultType;
import com.boonsan.domain.model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EndorsementApplicationService {

    private final ContractApplicationService contractApplicationService;
    private final UnderwritingRequestApplicationService underwritingRequestApplicationService;
    private final EndorsementMapper endorsementMapper;

    public EndorsementApplicationService(
            ContractApplicationService contractApplicationService,
            UnderwritingRequestApplicationService underwritingRequestApplicationService,
            EndorsementMapper endorsementMapper
    ) {
        this.contractApplicationService = contractApplicationService;
        this.underwritingRequestApplicationService = underwritingRequestApplicationService;
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
        requireUnderwritingResult(active, UnderwritingResultType.APPROVED, "approve");

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
