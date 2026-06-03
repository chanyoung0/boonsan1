package contract.mapper;

import contract.dto.MaturityTargetResponse;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MaturityContractMapper {

    int updateStatusToExpired(
            @Param("policyNumber") String policyNumber,
            @Param("currentStatus") String currentStatus,
            @Param("nextStatus") String nextStatus
    );

    List<MaturityTargetResponse> findMaturityTargets(@Param("targetEndDate") LocalDate targetEndDate);

    List<MaturityTargetResponse> findRenewalTargets();

    int upsertNotice(
            @Param("noticeId") String noticeId,
            @Param("policyNumber") String policyNumber,
            @Param("deliveryMethod") String deliveryMethod,
            @Param("noticeMessage") String noticeMessage,
            @Param("sentAt") LocalDateTime sentAt,
            @Param("createdAt") LocalDateTime createdAt
    );

    int updateRenewalIntention(
            @Param("policyNumber") String policyNumber,
            @Param("renewalIntention") boolean renewalIntention,
            @Param("renewalCheckedAt") LocalDateTime renewalCheckedAt
    );

    int updateContractStatus(
            @Param("policyNumber") String policyNumber,
            @Param("nextStatus") String nextStatus
    );
}
