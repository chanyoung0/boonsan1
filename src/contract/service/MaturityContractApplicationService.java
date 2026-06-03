package contract.service;

import contract.dto.MaturityNoticeResponse;
import contract.dto.MaturityProcessResponse;
import contract.dto.MaturityRenewalIntentionRequest;
import contract.dto.MaturityRenewalResponse;
import contract.dto.MaturityTargetResponse;
import contract.mapper.MaturityContractMapper;
import enums.ContractStatus;
import model.contract.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MaturityContractApplicationService {

    private static final String DEFAULT_DELIVERY_METHOD = "SMS";

    private final ContractApplicationService contractApplicationService;
    private final MaturityContractMapper maturityContractMapper;

    public MaturityContractApplicationService(
            ContractApplicationService contractApplicationService,
            MaturityContractMapper maturityContractMapper
    ) {
        this.contractApplicationService = contractApplicationService;
        this.maturityContractMapper = maturityContractMapper;
    }

    @Transactional(readOnly = true)
    public MaturityNoticeResponse getMaturityNotice(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        LocalDate today = LocalDate.now();
        long daysUntilMaturity = ChronoUnit.DAYS.between(today, contract.getContractEndDate());
        String noticeMessage = composeNoticeMessage(contract.getContractStatus(), daysUntilMaturity,
                contract.getContractEndDate());

        return new MaturityNoticeResponse(
                contract.getPolicyNumber(),
                contract.getInsuredName(),
                contract.getInsuredContact(),
                contract.getContractEndDate(),
                contract.getContractStatus(),
                daysUntilMaturity,
                noticeMessage,
                DEFAULT_DELIVERY_METHOD,
                contract.getMaturityRefundAmount(),
                null,
                null,
                null
        );
    }

    @Transactional(readOnly = true)
    public List<MaturityTargetResponse> listMaturityTargets() {
        return maturityContractMapper.findMaturityTargets(LocalDate.now().plusDays(30));
    }

    @Transactional(readOnly = true)
    public List<MaturityTargetResponse> listRenewalTargets() {
        return maturityContractMapper.findRenewalTargets();
    }

    @Transactional
    public MaturityNoticeResponse sendMaturityNotice(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        if (contract.getContractStatus() != ContractStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Maturity notice requires ACTIVE contract, but current status: " + contract.getContractStatus());
        }

        LocalDate today = LocalDate.now();
        long daysUntilMaturity = ChronoUnit.DAYS.between(today, contract.getContractEndDate());
        if (daysUntilMaturity > 30) {
            throw new IllegalArgumentException(
                    "Maturity notice is available within 30 days of contract end date: "
                            + contract.getContractEndDate());
        }

        String noticeMessage = composeNoticeMessage(
                contract.getContractStatus(),
                daysUntilMaturity,
                contract.getContractEndDate()
        );
        LocalDateTime sentAt = LocalDateTime.now();
        maturityContractMapper.upsertNotice(
                generateNoticeId(),
                contract.getPolicyNumber(),
                DEFAULT_DELIVERY_METHOD,
                noticeMessage,
                sentAt,
                sentAt
        );

        return new MaturityNoticeResponse(
                contract.getPolicyNumber(),
                contract.getInsuredName(),
                contract.getInsuredContact(),
                contract.getContractEndDate(),
                contract.getContractStatus(),
                daysUntilMaturity,
                noticeMessage,
                DEFAULT_DELIVERY_METHOD,
                contract.getMaturityRefundAmount(),
                sentAt,
                null,
                null
        );
    }

    @Transactional
    public MaturityRenewalResponse recordRenewalIntention(
            String policyNumber,
            MaturityRenewalIntentionRequest request
    ) {
        if (request == null || request.getRenewalIntention() == null) {
            throw new IllegalArgumentException("Renewal intention is required");
        }

        Contract contract = contractApplicationService.requireContract(policyNumber);
        if (contract.getContractEndDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Renewal intention can be finalized after the contract end date: "
                            + contract.getContractEndDate());
        }
        if (contract.getContractStatus() != ContractStatus.ACTIVE
                && contract.getContractStatus() != ContractStatus.MATURED) {
            throw new IllegalArgumentException(
                    "Renewal intention cannot be recorded for contract status: " + contract.getContractStatus());
        }

        LocalDateTime checkedAt = LocalDateTime.now();
        int noticeUpdated = maturityContractMapper.updateRenewalIntention(
                contract.getPolicyNumber(),
                request.getRenewalIntention(),
                checkedAt
        );
        if (noticeUpdated == 0) {
            throw new IllegalArgumentException(
                    "Maturity notice must be sent before recording renewal intention: " + contract.getPolicyNumber());
        }

        ContractStatus nextStatus = request.getRenewalIntention()
                ? ContractStatus.MATURED
                : ContractStatus.EXPIRED;
        maturityContractMapper.updateContractStatus(contract.getPolicyNumber(), nextStatus.name());

        String message = request.getRenewalIntention()
                ? "Renewal intention recorded. Contract remains in matured status."
                : "No renewal intention recorded. Contract moved to expired status.";
        return new MaturityRenewalResponse(
                contract.getPolicyNumber(),
                request.getRenewalIntention(),
                nextStatus,
                checkedAt,
                message
        );
    }

    @Transactional
    public MaturityProcessResponse processMaturity(String policyNumber) {
        Contract contract = contractApplicationService.requireContract(policyNumber);
        ContractStatus previousStatus = contract.getContractStatus();

        if (previousStatus != ContractStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Maturity processing requires ACTIVE contract, but current status: " + previousStatus);
        }

        LocalDate today = LocalDate.now();
        if (contract.getContractEndDate().isAfter(today)) {
            throw new IllegalArgumentException(
                    "Maturity processing requires contractEndDate <= today: " + contract.getContractEndDate());
        }

        int updated = maturityContractMapper.updateStatusToExpired(
                contract.getPolicyNumber(),
                ContractStatus.ACTIVE.name(),
                ContractStatus.MATURED.name()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Maturity processing failed (concurrent modification): " + contract.getPolicyNumber());
        }

        return new MaturityProcessResponse(
                contract.getPolicyNumber(),
                previousStatus,
                ContractStatus.MATURED,
                contract.getContractEndDate(),
                LocalDateTime.now(),
                "Maturity processing completed"
        );
    }

    private String composeNoticeMessage(ContractStatus status, long daysUntilMaturity, LocalDate endDate) {
        if (status == ContractStatus.EXPIRED) {
            return "This contract has already expired. (Maturity date: " + endDate + ")";
        }
        if (daysUntilMaturity > 30) {
            return daysUntilMaturity + " days remain until the maturity date (" + endDate + ").";
        }
        if (daysUntilMaturity > 0) {
            return "The contract will mature in " + daysUntilMaturity
                    + " days (" + endDate + "). Please confirm renewal intention.";
        }
        if (daysUntilMaturity == 0) {
            return "The contract matures today (" + endDate + "). Please process maturity.";
        }
        return Math.abs(daysUntilMaturity) + " days have passed since maturity (" + endDate
                + "). Maturity processing is required.";
    }

    private String generateNoticeId() {
        return "MAT-" + Year.now().getValue() + "-"
                + String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
    }
}
