package contract.mapper;

import contract.dto.ReinstatementResponse;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReinstatementMapper {

    void insertReinstatement(
            @Param("reinstatementId") String reinstatementId,
            @Param("policyNumber") String policyNumber,
            @Param("reinstatementReason") String reinstatementReason,
            @Param("desiredDate") LocalDate desiredDate,
            @Param("hasHealthChanged") Boolean hasHealthChanged,
            @Param("lastPaidDate") LocalDate lastPaidDate,
            @Param("unpaidInstallmentCount") Integer unpaidInstallmentCount,
            @Param("premiumPerInstallment") BigDecimal premiumPerInstallment,
            @Param("unpaidPremium") BigDecimal unpaidPremium,
            @Param("reinstatementStatus") String reinstatementStatus,
            @Param("appliedAt") LocalDateTime appliedAt
    );

    ReinstatementResponse findById(@Param("reinstatementId") String reinstatementId);

    ReinstatementResponse findActiveByPolicyNumber(@Param("policyNumber") String policyNumber);

    List<ReinstatementResponse> findByPolicyNumber(@Param("policyNumber") String policyNumber);

    int updateStatusFromAppliedToSettled(
            @Param("reinstatementId") String reinstatementId,
            @Param("unpaidSettledAt") LocalDateTime unpaidSettledAt
    );

    int updateStatusFromSettledToCompleted(
            @Param("reinstatementId") String reinstatementId,
            @Param("completedAt") LocalDateTime completedAt
    );

    int updateStatusToCancelled(
            @Param("reinstatementId") String reinstatementId,
            @Param("cancelledAt") LocalDateTime cancelledAt
    );

    int updateContractStatusToActive(
            @Param("policyNumber") String policyNumber,
            @Param("currentStatus") String currentStatus,
            @Param("nextStatus") String nextStatus
    );

    int updateUnderwritingRequestId(
            @Param("reinstatementId") String reinstatementId,
            @Param("underwritingRequestId") String underwritingRequestId
    );
}
