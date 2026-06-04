package com.boonsan.domain.underwriting.dto;

import java.time.LocalDateTime;

public class UnderwritingReviewResponse {

    private String applicationId;
    private String reviewId;
    private String underwritingStatus;
    private String underwritingType;
    private float totalScore;
    private int totalDeduction;
    private String recommendedResult;
    private String finalResult;
    private boolean autoReviewAvailable;
    private boolean coinsuranceRecommended;
    private String itemizedScores;
    private String underwriterId;
    private String underwriterName;
    private String department;
    private String underwritingOpinion;
    private String surchargeCondition;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime finalizedAt;
    private String nextStepMessage;

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getUnderwritingStatus() { return underwritingStatus; }
    public void setUnderwritingStatus(String underwritingStatus) { this.underwritingStatus = underwritingStatus; }
    public String getUnderwritingType() { return underwritingType; }
    public void setUnderwritingType(String underwritingType) { this.underwritingType = underwritingType; }
    public float getTotalScore() { return totalScore; }
    public void setTotalScore(float totalScore) { this.totalScore = totalScore; }
    public int getTotalDeduction() { return totalDeduction; }
    public void setTotalDeduction(int totalDeduction) { this.totalDeduction = totalDeduction; }
    public String getRecommendedResult() { return recommendedResult; }
    public void setRecommendedResult(String recommendedResult) { this.recommendedResult = recommendedResult; }
    public String getFinalResult() { return finalResult; }
    public void setFinalResult(String finalResult) { this.finalResult = finalResult; }
    public boolean isAutoReviewAvailable() { return autoReviewAvailable; }
    public void setAutoReviewAvailable(boolean autoReviewAvailable) { this.autoReviewAvailable = autoReviewAvailable; }
    public boolean isCoinsuranceRecommended() { return coinsuranceRecommended; }
    public void setCoinsuranceRecommended(boolean coinsuranceRecommended) { this.coinsuranceRecommended = coinsuranceRecommended; }
    public String getItemizedScores() { return itemizedScores; }
    public void setItemizedScores(String itemizedScores) { this.itemizedScores = itemizedScores; }
    public String getUnderwriterId() { return underwriterId; }
    public void setUnderwriterId(String underwriterId) { this.underwriterId = underwriterId; }
    public String getUnderwriterName() { return underwriterName; }
    public void setUnderwriterName(String underwriterName) { this.underwriterName = underwriterName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getUnderwritingOpinion() { return underwritingOpinion; }
    public void setUnderwritingOpinion(String underwritingOpinion) { this.underwritingOpinion = underwritingOpinion; }
    public String getSurchargeCondition() { return surchargeCondition; }
    public void setSurchargeCondition(String surchargeCondition) { this.surchargeCondition = surchargeCondition; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getFinalizedAt() { return finalizedAt; }
    public void setFinalizedAt(LocalDateTime finalizedAt) { this.finalizedAt = finalizedAt; }
    public String getNextStepMessage() { return nextStepMessage; }
    public void setNextStepMessage(String nextStepMessage) { this.nextStepMessage = nextStepMessage; }
}
