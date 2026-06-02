package underwriting.dto;

import java.time.LocalDateTime;

public class CreditInformationInquiryResponse {

    private String inquiryId;
    private String applicationId;
    private String customerName;
    private String customerIdentifierMasked;
    private boolean accidentHistoryExists;
    private boolean otherInsuranceContractExists;
    private boolean previousClaimExists;
    private String creditRiskGrade;
    private String riskFlags;
    private String inquiryStatus;
    private String externalSystemMessage;
    private LocalDateTime createdAt;

    public String getInquiryId() { return inquiryId; }
    public void setInquiryId(String inquiryId) { this.inquiryId = inquiryId; }
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerIdentifierMasked() { return customerIdentifierMasked; }
    public void setCustomerIdentifierMasked(String customerIdentifierMasked) { this.customerIdentifierMasked = customerIdentifierMasked; }
    public boolean isAccidentHistoryExists() { return accidentHistoryExists; }
    public void setAccidentHistoryExists(boolean accidentHistoryExists) { this.accidentHistoryExists = accidentHistoryExists; }
    public boolean isOtherInsuranceContractExists() { return otherInsuranceContractExists; }
    public void setOtherInsuranceContractExists(boolean otherInsuranceContractExists) { this.otherInsuranceContractExists = otherInsuranceContractExists; }
    public boolean isPreviousClaimExists() { return previousClaimExists; }
    public void setPreviousClaimExists(boolean previousClaimExists) { this.previousClaimExists = previousClaimExists; }
    public String getCreditRiskGrade() { return creditRiskGrade; }
    public void setCreditRiskGrade(String creditRiskGrade) { this.creditRiskGrade = creditRiskGrade; }
    public String getRiskFlags() { return riskFlags; }
    public void setRiskFlags(String riskFlags) { this.riskFlags = riskFlags; }
    public String getInquiryStatus() { return inquiryStatus; }
    public void setInquiryStatus(String inquiryStatus) { this.inquiryStatus = inquiryStatus; }
    public String getExternalSystemMessage() { return externalSystemMessage; }
    public void setExternalSystemMessage(String externalSystemMessage) { this.externalSystemMessage = externalSystemMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
