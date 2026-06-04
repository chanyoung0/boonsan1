package com.boonsan.domain.underwriting.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReinsuranceProcessResponse {

    private String processId;
    private String applicationId;
    private boolean reinsuranceRequired;
    private String reinsuranceReason;
    private String reinsurerName;
    private String requestStatus;
    private String resultStatus;
    private BigDecimal retentionAmount;
    private BigDecimal cessionRate;
    private String rejectionReason;
    private String externalSystemMessage;
    private LocalDateTime requestedAt;
    private LocalDateTime resultRegisteredAt;
    private LocalDateTime updatedAt;

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public boolean isReinsuranceRequired() { return reinsuranceRequired; }
    public void setReinsuranceRequired(boolean reinsuranceRequired) { this.reinsuranceRequired = reinsuranceRequired; }
    public String getReinsuranceReason() { return reinsuranceReason; }
    public void setReinsuranceReason(String reinsuranceReason) { this.reinsuranceReason = reinsuranceReason; }
    public String getReinsurerName() { return reinsurerName; }
    public void setReinsurerName(String reinsurerName) { this.reinsurerName = reinsurerName; }
    public String getRequestStatus() { return requestStatus; }
    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }
    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    public BigDecimal getRetentionAmount() { return retentionAmount; }
    public void setRetentionAmount(BigDecimal retentionAmount) { this.retentionAmount = retentionAmount; }
    public BigDecimal getCessionRate() { return cessionRate; }
    public void setCessionRate(BigDecimal cessionRate) { this.cessionRate = cessionRate; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getExternalSystemMessage() { return externalSystemMessage; }
    public void setExternalSystemMessage(String externalSystemMessage) { this.externalSystemMessage = externalSystemMessage; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getResultRegisteredAt() { return resultRegisteredAt; }
    public void setResultRegisteredAt(LocalDateTime resultRegisteredAt) { this.resultRegisteredAt = resultRegisteredAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
