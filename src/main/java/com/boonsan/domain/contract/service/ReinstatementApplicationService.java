package com.boonsan.domain.contract.service;

import com.boonsan.domain.contract.dto.ReinstatementCreateRequest;
import com.boonsan.domain.contract.dto.ReinstatementResponse;
import com.boonsan.domain.contract.dto.UnderwritingRequestCompleteRequest;
import com.boonsan.domain.contract.dto.UnderwritingRequestCreateRequest;
import com.boonsan.domain.contract.dto.UnderwritingRequestResponse;
import com.boonsan.domain.contract.mapper.ReinstatementMapper;
import com.boonsan.domain.enums.ContractStatus;
import com.boonsan.domain.enums.ReinstatementStatus;
import com.boonsan.domain.enums.RequestReason;
import com.boonsan.domain.enums.RequestStatus;
import com.boonsan.domain.enums.UnderwritingResultType;
import com.boonsan.domain.model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReinstatementApplicationService {

    private final ContractApplicationService contractApplicationService;
    private final UnderwritingRequestApplicationService underwritingRequestApplicationService;
    private final ReinstatementMapper reinstatementMapper;

    public ReinstatementApplicationService(
            ContractApplicationService contractApplicationService,
            UnderwritingRequestApplicationService underwritingRequestApplicationService,
            ReinstatementMapper reinstatementMapper
    ) {
        this.contractApplicationService = contractApplicationService;
        this.underwritingRequestApplicationService = underwritingRequestApplicationService;
        this.reinstatementMapper = reinstatementMapper;
    }

    @Transactional
    public ReinstatementResponse apply(String policyNumber, ReinstatementCreateRequest request) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        if (contract.getContractStatus() != ContractStatus.SUSPENDED) {
            throw new IllegalArgumentException(
                    "Reinstatement requires SUSPENDED contract. Current: " + contract.getContractStatus());
        }

        ReinstatementResponse active = reinstatementMapper.findActiveByPolicyNumber(contract.getPolicyNumber());
        if (active != null) {
            throw new IllegalArgumentException(
                    "Active reinstatement already exists: " + active.getReinstatementId()
                            + " (" + active.getReinstatementStatus() + ")");
        }

        if (request.getReinstatementReason() == null) {
            throw new IllegalArgumentException("reinstatementReason is required.");
        }
        if (request.getDesiredDate() == null) {
            throw new IllegalArgumentException("desiredDate is required.");
        }
        if (request.getHasHealthChanged() == null) {
            throw new IllegalArgumentException("hasHealthChanged is required.");
        }
        if (request.getUnpaidInstallmentCount() == null || request.getUnpaidInstallmentCount() < 1) {
            throw new IllegalArgumentException("unpaidInstallmentCount must be >= 1.");
        }
        if (request.getPremiumPerInstallment() == null || request.getPremiumPerInstallment().signum() <= 0) {
            throw new IllegalArgumentException("premiumPerInstallment must be greater than 0.");
        }

        // 미납보험료 = 보험료 × 미납회차 (insurance-system-architecture 스킬 명시)
        BigDecimal unpaidPremium = request.getPremiumPerInstallment()
                .multiply(BigDecimal.valueOf(request.getUnpaidInstallmentCount()));

        LocalDateTime appliedAt = LocalDateTime.now();
        String reinstatementId = generateReinstatementId();

        reinstatementMapper.insertReinstatement(
                reinstatementId,
                contract.getPolicyNumber(),
                request.getReinstatementReason().name(),
                request.getDesiredDate(),
                request.getHasHealthChanged(),
                request.getLastPaidDate(),
                request.getUnpaidInstallmentCount(),
                request.getPremiumPerInstallment(),
                unpaidPremium,
                ReinstatementStatus.APPLIED.name(),
                appliedAt
        );

        ReinstatementResponse saved = reinstatementMapper.findById(reinstatementId);
        if (saved == null) {
            throw new NoSuchElementException("Reinstatement not found after insert: " + reinstatementId);
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public ReinstatementResponse getActive(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        ReinstatementResponse active = reinstatementMapper.findActiveByPolicyNumber(contract.getPolicyNumber());
        if (active == null) {
            throw new NoSuchElementException("No active reinstatement for contract: " + contract.getPolicyNumber());
        }
        return active;
    }

    @Transactional(readOnly = true)
    public List<ReinstatementResponse> listByPolicyNumber(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        return reinstatementMapper.findByPolicyNumber(contract.getPolicyNumber());
    }

    @Transactional
    public ReinstatementResponse settleUnpaid(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        ReinstatementResponse active = requireActive(contract.getPolicyNumber());
        if (active.getReinstatementStatus() != ReinstatementStatus.APPLIED) {
            throw new IllegalArgumentException(
                    "settleUnpaid requires APPLIED status. Current: " + active.getReinstatementStatus());
        }
        // 심사요청이 연결돼 있으면 결과 APPROVED 필요. 연결 X(슬라이스 4 이전 데이터)면 통과.
        if (active.getUnderwritingRequestId() != null) {
            UnderwritingRequestResponse uw = underwritingRequestApplicationService
                    .requireById(active.getUnderwritingRequestId());
            if (uw.getRequestStatus() != RequestStatus.COMPLETED) {
                throw new IllegalArgumentException(
                        "Underwriting request must be COMPLETED before settling unpaid. Current: "
                                + uw.getRequestStatus());
            }
            if (uw.getUnderwritingResult() != UnderwritingResultType.APPROVED) {
                throw new IllegalArgumentException(
                        "Underwriting result must be APPROVED before settling unpaid. Current: "
                                + uw.getUnderwritingResult());
            }
        }

        int updated = reinstatementMapper.updateStatusFromAppliedToSettled(
                active.getReinstatementId(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Settle unpaid failed (concurrent modification): " + active.getReinstatementId());
        }
        return reinstatementMapper.findById(active.getReinstatementId());
    }

    @Transactional
    public UnderwritingRequestResponse requestUnderwriting(
            String policyNumber,
            UnderwritingRequestCreateRequest request
    ) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        ReinstatementResponse active = requireActive(contract.getPolicyNumber());
        if (active.getUnderwritingRequestId() != null) {
            throw new IllegalArgumentException(
                    "Underwriting request already exists for reinstatement: " + active.getReinstatementId());
        }
        if (active.getReinstatementStatus() != ReinstatementStatus.APPLIED) {
            throw new IllegalArgumentException(
                    "Underwriting can only be requested for APPLIED reinstatement. Current: "
                            + active.getReinstatementStatus());
        }

        UnderwritingRequestResponse uw = underwritingRequestApplicationService.createForSource(
                contract.getPolicyNumber(),
                RequestReason.REINSTATEMENT,
                active.getReinstatementId(),
                request == null ? null : request.getUnderwritingType()
        );
        int updated = reinstatementMapper.updateUnderwritingRequestId(
                active.getReinstatementId(),
                uw.getRequestId()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Link underwriting request failed (concurrent modification): " + active.getReinstatementId());
        }
        return uw;
    }

    @Transactional
    public UnderwritingRequestResponse completeUnderwriting(
            String policyNumber,
            UnderwritingRequestCompleteRequest request
    ) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        ReinstatementResponse active = requireActive(contract.getPolicyNumber());
        if (active.getUnderwritingRequestId() == null) {
            throw new IllegalArgumentException(
                    "No underwriting request linked to reinstatement: " + active.getReinstatementId());
        }
        return underwritingRequestApplicationService.complete(active.getUnderwritingRequestId(), request);
    }

    @Transactional(readOnly = true)
    public UnderwritingRequestResponse getUnderwritingRequest(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        ReinstatementResponse active = requireActive(contract.getPolicyNumber());
        if (active.getUnderwritingRequestId() == null) {
            throw new NoSuchElementException(
                    "No underwriting request linked to reinstatement: " + active.getReinstatementId());
        }
        return underwritingRequestApplicationService.findById(active.getUnderwritingRequestId());
    }

    @Transactional
    public ReinstatementResponse complete(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        if (contract.getContractStatus() != ContractStatus.SUSPENDED) {
            throw new IllegalArgumentException(
                    "Reinstatement complete requires SUSPENDED contract. Current: " + contract.getContractStatus());
        }
        ReinstatementResponse active = requireActive(contract.getPolicyNumber());
        if (active.getReinstatementStatus() != ReinstatementStatus.UNPAID_SETTLED) {
            throw new IllegalArgumentException(
                    "complete requires UNPAID_SETTLED status. Current: " + active.getReinstatementStatus());
        }

        int contractUpdated = reinstatementMapper.updateContractStatusToActive(
                contract.getPolicyNumber(),
                ContractStatus.SUSPENDED.name(),
                ContractStatus.ACTIVE.name()
        );
        if (contractUpdated == 0) {
            throw new IllegalArgumentException(
                    "Contract activation failed (concurrent modification): " + contract.getPolicyNumber());
        }

        int reinstatementUpdated = reinstatementMapper.updateStatusFromSettledToCompleted(
                active.getReinstatementId(),
                LocalDateTime.now()
        );
        if (reinstatementUpdated == 0) {
            throw new IllegalArgumentException(
                    "Reinstatement complete failed (concurrent modification): " + active.getReinstatementId());
        }
        return reinstatementMapper.findById(active.getReinstatementId());
    }

    @Transactional
    public ReinstatementResponse cancel(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        ReinstatementResponse active = requireActive(contract.getPolicyNumber());

        int updated = reinstatementMapper.updateStatusToCancelled(
                active.getReinstatementId(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Reinstatement cancel failed (concurrent modification): " + active.getReinstatementId());
        }
        return reinstatementMapper.findById(active.getReinstatementId());
    }

    private ReinstatementResponse requireActive(String policyNumber) {
        ReinstatementResponse active = reinstatementMapper.findActiveByPolicyNumber(policyNumber);
        if (active == null) {
            throw new NoSuchElementException("No active reinstatement for contract: " + policyNumber);
        }
        return active;
    }

    private String generateReinstatementId() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "RST-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }
}
