package com.boonsan.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthorizationCreateRequest {

    @NotBlank
    private String requestReason;

    @NotBlank
    @Size(max = 255)
    private String submissionAgencyName;

    @Size(max = 255)
    private String productDescriptionFileName;

    @Size(max = 255)
    private String termsAndConditionsFileName;

    @Size(max = 255)
    private String rateScheduleFileName;

    @Size(max = 255)
    private String productEvidenceFileName;

    public String getRequestReason() { return requestReason; }
    public void setRequestReason(String requestReason) { this.requestReason = requestReason; }

    public String getSubmissionAgencyName() { return submissionAgencyName; }
    public void setSubmissionAgencyName(String submissionAgencyName) { this.submissionAgencyName = submissionAgencyName; }

    public String getProductDescriptionFileName() { return productDescriptionFileName; }
    public void setProductDescriptionFileName(String productDescriptionFileName) { this.productDescriptionFileName = productDescriptionFileName; }

    public String getTermsAndConditionsFileName() { return termsAndConditionsFileName; }
    public void setTermsAndConditionsFileName(String termsAndConditionsFileName) { this.termsAndConditionsFileName = termsAndConditionsFileName; }

    public String getRateScheduleFileName() { return rateScheduleFileName; }
    public void setRateScheduleFileName(String rateScheduleFileName) { this.rateScheduleFileName = rateScheduleFileName; }

    public String getProductEvidenceFileName() { return productEvidenceFileName; }
    public void setProductEvidenceFileName(String productEvidenceFileName) { this.productEvidenceFileName = productEvidenceFileName; }
}
