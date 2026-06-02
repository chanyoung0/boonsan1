package underwriting.dto;

public class UnderwritingFollowUpEligibilityResponse {

    private String applicationId;
    private boolean eligible;
    private String reason;
    private String applicationStatus;
    private String finalResult;
    private Float totalScore;
    private boolean coinsuranceRecommended;
    private boolean reinsuranceRequired;
    private String processStatus;
    private String resultStatus;
    private String policyNumber;
    private String nextStepMessage;

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    public String getFinalResult() { return finalResult; }
    public void setFinalResult(String finalResult) { this.finalResult = finalResult; }
    public Float getTotalScore() { return totalScore; }
    public void setTotalScore(Float totalScore) { this.totalScore = totalScore; }
    public boolean isCoinsuranceRecommended() { return coinsuranceRecommended; }
    public void setCoinsuranceRecommended(boolean coinsuranceRecommended) { this.coinsuranceRecommended = coinsuranceRecommended; }
    public boolean isReinsuranceRequired() { return reinsuranceRequired; }
    public void setReinsuranceRequired(boolean reinsuranceRequired) { this.reinsuranceRequired = reinsuranceRequired; }
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public String getNextStepMessage() { return nextStepMessage; }
    public void setNextStepMessage(String nextStepMessage) { this.nextStepMessage = nextStepMessage; }
}
