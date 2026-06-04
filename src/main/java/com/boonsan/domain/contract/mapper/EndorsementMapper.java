package com.boonsan.domain.contract.mapper;

import com.boonsan.domain.contract.dto.EndorsementResponse;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EndorsementMapper {

    void insertEndorsement(
            @Param("endorsementId") String endorsementId,
            @Param("policyNumber") String policyNumber,
            @Param("endorsementType") String endorsementType,
            @Param("changeReason") String changeReason,
            @Param("previousContent") String previousContent,
            @Param("newContent") String newContent,
            @Param("endorsementStatus") String endorsementStatus,
            @Param("appliedAt") LocalDateTime appliedAt
    );

    EndorsementResponse findById(@Param("endorsementId") String endorsementId);

    EndorsementResponse findActiveByPolicyNumber(@Param("policyNumber") String policyNumber);

    List<EndorsementResponse> findByPolicyNumber(@Param("policyNumber") String policyNumber);

    int updateUnderwritingRequestId(
            @Param("endorsementId") String endorsementId,
            @Param("underwritingRequestId") String underwritingRequestId
    );

    int updateStatusToApproved(
            @Param("endorsementId") String endorsementId,
            @Param("approvedAt") LocalDateTime approvedAt
    );

    int updateStatusToRejected(
            @Param("endorsementId") String endorsementId,
            @Param("rejectedAt") LocalDateTime rejectedAt
    );

    int updateStatusToCancelled(
            @Param("endorsementId") String endorsementId,
            @Param("cancelledAt") LocalDateTime cancelledAt
    );
}
