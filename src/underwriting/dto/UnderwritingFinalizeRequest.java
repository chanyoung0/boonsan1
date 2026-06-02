package underwriting.dto;

import jakarta.validation.constraints.NotBlank;

public class UnderwritingFinalizeRequest {

    @NotBlank
    private String finalResult;
    @NotBlank
    private String underwriterId;
    @NotBlank
    private String underwriterName;
    @NotBlank
    private String department;
    private String underwritingOpinion;
    private String surchargeCondition;
    private String rejectionReason;

    public String getFinalResult() { return finalResult; }
    public void setFinalResult(String finalResult) { this.finalResult = finalResult; }
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
}
