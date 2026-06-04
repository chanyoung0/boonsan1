package com.boonsan.underwriting.dto;

import java.time.LocalDateTime;

public class UnderwritingHistoryResponse {

    private String historyId;
    private String applicationId;
    private String reviewId;
    private String eventType;
    private String eventMessage;
    private Float score;
    private String result;
    private LocalDateTime createdAt;

    public String getHistoryId() { return historyId; }
    public void setHistoryId(String historyId) { this.historyId = historyId; }
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getEventMessage() { return eventMessage; }
    public void setEventMessage(String eventMessage) { this.eventMessage = eventMessage; }
    public Float getScore() { return score; }
    public void setScore(Float score) { this.score = score; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
