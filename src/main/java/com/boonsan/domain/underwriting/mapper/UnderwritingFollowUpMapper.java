package com.boonsan.domain.underwriting.mapper;

import org.apache.ibatis.annotations.Param;
import com.boonsan.domain.underwriting.dto.CoinsuranceProcessResponse;
import com.boonsan.domain.underwriting.dto.PolicyIssueResponse;
import com.boonsan.domain.underwriting.dto.ReinsuranceProcessResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface UnderwritingFollowUpMapper {

    void insertCoinsuranceProcess(
            @Param("processId") String processId,
            @Param("applicationId") String applicationId,
            @Param("coinsurerName") String coinsurerName,
            @Param("requestStatus") String requestStatus,
            @Param("resultStatus") String resultStatus,
            @Param("retainedAmount") BigDecimal retainedAmount,
            @Param("shareRate") BigDecimal shareRate,
            @Param("manualSelected") boolean manualSelected,
            @Param("externalSystemMessage") String externalSystemMessage,
            @Param("requestedAt") LocalDateTime requestedAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    CoinsuranceProcessResponse findCoinsuranceByApplicationId(@Param("applicationId") String applicationId);

    int updateCoinsuranceResult(
            @Param("applicationId") String applicationId,
            @Param("resultStatus") String resultStatus,
            @Param("rejectionReason") String rejectionReason,
            @Param("resultRegisteredAt") LocalDateTime resultRegisteredAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    void insertReinsuranceProcess(
            @Param("processId") String processId,
            @Param("applicationId") String applicationId,
            @Param("reinsuranceRequired") boolean reinsuranceRequired,
            @Param("reinsuranceReason") String reinsuranceReason,
            @Param("reinsurerName") String reinsurerName,
            @Param("requestStatus") String requestStatus,
            @Param("resultStatus") String resultStatus,
            @Param("retentionAmount") BigDecimal retentionAmount,
            @Param("cessionRate") BigDecimal cessionRate,
            @Param("externalSystemMessage") String externalSystemMessage,
            @Param("requestedAt") LocalDateTime requestedAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    ReinsuranceProcessResponse findReinsuranceByApplicationId(@Param("applicationId") String applicationId);

    int updateReinsuranceResult(
            @Param("applicationId") String applicationId,
            @Param("resultStatus") String resultStatus,
            @Param("rejectionReason") String rejectionReason,
            @Param("resultRegisteredAt") LocalDateTime resultRegisteredAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    void insertPolicyIssue(
            @Param("issueId") String issueId,
            @Param("applicationId") String applicationId,
            @Param("policyNumber") String policyNumber,
            @Param("issueStatus") String issueStatus,
            @Param("finalResult") String finalResult,
            @Param("appliedCondition") String appliedCondition,
            @Param("externalSystemMessage") String externalSystemMessage,
            @Param("issuedAt") LocalDateTime issuedAt
    );

    PolicyIssueResponse findPolicyIssueByApplicationId(@Param("applicationId") String applicationId);
}
