package com.boonsan.domain.contract.mapper;

import com.boonsan.domain.contract.dto.UnderwritingRequestResponse;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UnderwritingRequestMapper {

    void insertRequest(
            @Param("requestId") String requestId,
            @Param("policyNumber") String policyNumber,
            @Param("requestReason") String requestReason,
            @Param("sourceId") String sourceId,
            @Param("underwritingType") String underwritingType,
            @Param("requestStatus") String requestStatus,
            @Param("requestedAt") LocalDateTime requestedAt
    );

    UnderwritingRequestResponse findById(@Param("requestId") String requestId);

    List<UnderwritingRequestResponse> findBySourceId(@Param("sourceId") String sourceId);

    int updateCompleteResult(
            @Param("requestId") String requestId,
            @Param("underwritingResult") String underwritingResult,
            @Param("rejectionReason") String rejectionReason,
            @Param("surchargeCondition") String surchargeCondition,
            @Param("completedAt") LocalDateTime completedAt
    );

    int updateStatusToCancelled(
            @Param("requestId") String requestId,
            @Param("cancelledAt") LocalDateTime cancelledAt
    );
}
