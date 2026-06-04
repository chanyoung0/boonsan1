package com.boonsan.domain.underwriting.dto;

import jakarta.validation.constraints.Size;

public class CreditInformationInquiryCreateRequest {

    @Size(max = 100)
    private String customerName;
    @Size(max = 100)
    private String customerIdentifier;
    private Boolean accidentHistoryExists;
    private Boolean otherInsuranceContractExists;
    private Boolean previousClaimExists;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerIdentifier() { return customerIdentifier; }
    public void setCustomerIdentifier(String customerIdentifier) { this.customerIdentifier = customerIdentifier; }
    public Boolean getAccidentHistoryExists() { return accidentHistoryExists; }
    public void setAccidentHistoryExists(Boolean accidentHistoryExists) { this.accidentHistoryExists = accidentHistoryExists; }
    public Boolean getOtherInsuranceContractExists() { return otherInsuranceContractExists; }
    public void setOtherInsuranceContractExists(Boolean otherInsuranceContractExists) { this.otherInsuranceContractExists = otherInsuranceContractExists; }
    public Boolean getPreviousClaimExists() { return previousClaimExists; }
    public void setPreviousClaimExists(Boolean previousClaimExists) { this.previousClaimExists = previousClaimExists; }
}
