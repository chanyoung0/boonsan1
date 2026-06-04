package com.boonsan.domain.underwriting.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CoinsuranceProcessResponse {

    private String processId;
    private String applicationId;
    private String coinsurerName;
    private String requestStatus;
    private String resultStatus;
    private BigDecimal retainedAmount;
    private BigDecimal shareRate;
    private boolean manualSelected;
    private String rejectionReason;
    private String externalSystemMessage;
    private LocalDateTime requestedAt;
    private LocalDateTime resultRegisteredAt;
    private LocalDateTime updatedAt;

    public String getProcessId() { return processId; }
    public void setProcessId(String processId) { this.processId = processId; }
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getCoinsurerName() { return coinsurerName; }
    public void setCoinsurerName(String coinsurerName) { this.coinsurerName = coinsurerName; }
    public String getRequestStatus() { return requestStatus; }
    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }
    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    public BigDecimal getRetainedAmount() { return retainedAmount; }
    public void setRetainedAmount(BigDecimal retainedAmount) { this.retainedAmount = retainedAmount; }
    public BigDecimal getShareRate() { return shareRate; }
    public void setShareRate(BigDecimal shareRate) { this.shareRate = shareRate; }
    public boolean isManualSelected() { return manualSelected; }
    public void setManualSelected(boolean manualSelected) { this.manualSelected = manualSelected; }
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
