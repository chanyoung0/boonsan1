package com.boonsan.domain.underwriting.dto;

import jakarta.validation.constraints.NotBlank;

public class CoinsuranceResultRequest {

    @NotBlank
    private String resultStatus;
    private String rejectionReason;

    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
