package com.boonsan.domain.contract.mapper;

import com.boonsan.domain.contract.dto.PayoutResponse;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PayoutMapper {

    void insertPayout(
            @Param("payoutId") String payoutId,
            @Param("policyNumber") String policyNumber,
            @Param("calculationBasis") String calculationBasis,
            @Param("paymentType") String paymentType,
            @Param("paidPremiumAmount") BigDecimal paidPremiumAmount,
            @Param("refundRate") BigDecimal refundRate,
            @Param("calculatedAmount") BigDecimal calculatedAmount,
            @Param("deductionItem") String deductionItem,
            @Param("deductionAmount") BigDecimal deductionAmount,
            @Param("finalPaymentAmount") BigDecimal finalPaymentAmount,
            @Param("payoutStatus") String payoutStatus,
            @Param("createdAt") LocalDateTime createdAt
    );

    PayoutResponse findByPayoutId(@Param("payoutId") String payoutId);

    List<PayoutResponse> findByPolicyNumber(@Param("policyNumber") String policyNumber);

    int updateStatusToApproved(
            @Param("payoutId") String payoutId,
            @Param("currentStatus") String currentStatus,
            @Param("nextStatus") String nextStatus,
            @Param("processor") String processor,
            @Param("approvedAt") LocalDateTime approvedAt
    );

    int updateStatusToPaid(
            @Param("payoutId") String payoutId,
            @Param("currentStatus") String currentStatus,
            @Param("nextStatus") String nextStatus,
            @Param("paidAt") LocalDateTime paidAt
    );

    int updateStatusToCancelled(
            @Param("payoutId") String payoutId,
            @Param("nextStatus") String nextStatus,
            @Param("cancelledAt") LocalDateTime cancelledAt
    );
}
