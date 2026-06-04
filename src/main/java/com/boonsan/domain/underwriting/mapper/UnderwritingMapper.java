package com.boonsan.domain.underwriting.mapper;

import org.apache.ibatis.annotations.Param;
import com.boonsan.domain.underwriting.dto.UnderwritingApplicationResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingHistoryResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingReviewResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface UnderwritingMapper {

    void insertApplication(
            @Param("applicationId") String applicationId,
            @Param("applicationStatus") String applicationStatus,
            @Param("appliedAt") LocalDateTime appliedAt,
            @Param("appliedCondition") String appliedCondition,
            @Param("insuredAmount") BigDecimal insuredAmount,
            @Param("insuredPersonInfo") String insuredPersonInfo,
            @Param("paymentCycle") String paymentCycle,
            @Param("premium") BigDecimal premium,
            @Param("productCode") String productCode,
            @Param("specialContractList") String specialContractList,
            @Param("termsVersion") String termsVersion,
            @Param("insuredPersonName") String insuredPersonName,
            @Param("age") Integer age,
            @Param("gender") String gender,
            @Param("occupation") String occupation,
            @Param("annualIncome") BigDecimal annualIncome,
            @Param("pastMedicalHistory") String pastMedicalHistory,
            @Param("medicated") boolean medicated,
            @Param("surgeryHistory") String surgeryHistory,
            @Param("familyHistory") String familyHistory,
            @Param("smoker") boolean smoker,
            @Param("alcoholConsumption") String alcoholConsumption,
            @Param("bmi") BigDecimal bmi,
            @Param("vehicleModel") String vehicleModel,
            @Param("vehicleNumber") String vehicleNumber,
            @Param("hasAccidentHistory") boolean hasAccidentHistory,
            @Param("hasOtherContract") boolean hasOtherContract
    );

    UnderwritingApplicationResponse findApplicationById(@Param("applicationId") String applicationId);

    void insertReview(
            @Param("reviewId") String reviewId,
            @Param("applicationId") String applicationId,
            @Param("underwritingStatus") String underwritingStatus,
            @Param("underwritingType") String underwritingType,
            @Param("autoScore") float autoScore,
            @Param("totalDeduction") int totalDeduction,
            @Param("recommendedResult") String recommendedResult,
            @Param("autoReviewAvailable") boolean autoReviewAvailable,
            @Param("coinsuranceRecommended") boolean coinsuranceRecommended,
            @Param("itemizedScores") String itemizedScores,
            @Param("createdAt") LocalDateTime createdAt
    );

    UnderwritingReviewResponse findLatestReviewByApplicationId(@Param("applicationId") String applicationId);

    int finalizeLatestReview(
            @Param("applicationId") String applicationId,
            @Param("finalResult") String finalResult,
            @Param("underwritingStatus") String underwritingStatus,
            @Param("underwriterId") String underwriterId,
            @Param("underwriterName") String underwriterName,
            @Param("department") String department,
            @Param("underwritingOpinion") String underwritingOpinion,
            @Param("surchargeCondition") String surchargeCondition,
            @Param("rejectionReason") String rejectionReason,
            @Param("finalizedAt") LocalDateTime finalizedAt
    );

    int updateApplicationAfterFinalReview(
            @Param("applicationId") String applicationId,
            @Param("applicationStatus") String applicationStatus,
            @Param("appliedCondition") String appliedCondition
    );

    void insertHistory(
            @Param("historyId") String historyId,
            @Param("applicationId") String applicationId,
            @Param("reviewId") String reviewId,
            @Param("eventType") String eventType,
            @Param("eventMessage") String eventMessage,
            @Param("score") Float score,
            @Param("result") String result,
            @Param("createdAt") LocalDateTime createdAt
    );

    List<UnderwritingHistoryResponse> findHistoryByApplicationId(@Param("applicationId") String applicationId);
}
