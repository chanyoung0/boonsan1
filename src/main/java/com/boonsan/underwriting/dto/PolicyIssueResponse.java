package com.boonsan.underwriting.dto;

import java.time.LocalDateTime;

public class PolicyIssueResponse {

    private String issueId;
    private String applicationId;
    private String policyNumber;
    private String issueStatus;
    private String finalResult;
    private String appliedCondition;
    private String externalSystemMessage;
    private LocalDateTime issuedAt;

    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public String getIssueStatus() { return issueStatus; }
    public void setIssueStatus(String issueStatus) { this.issueStatus = issueStatus; }
    public String getFinalResult() { return finalResult; }
    public void setFinalResult(String finalResult) { this.finalResult = finalResult; }
    public String getAppliedCondition() { return appliedCondition; }
    public void setAppliedCondition(String appliedCondition) { this.appliedCondition = appliedCondition; }
    public String getExternalSystemMessage() { return externalSystemMessage; }
    public void setExternalSystemMessage(String externalSystemMessage) { this.externalSystemMessage = externalSystemMessage; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}
