package com.boonsan.model.insurance;

import com.boonsan.enums.AuthorizationStatus;

import java.time.LocalDateTime;

// 상품 인가 도메인 모델 — 금융감독원 인가 요청 및 결과 반영 정보 관리
public class Authorization {

    private String requestId;
    private String productCode;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private boolean isApproved;
    private String requestReason;
    private String submissionAgencyName;
    private AuthorizationStatus authorizationStatus;
    private String productDescriptionFileName;
    private String termsAndConditionsFileName;
    private String rateScheduleFileName;
    private String productEvidenceFileName;
    private String revisionRequest;
    private LocalDateTime updatedAt;

    public Authorization() {}

    public Authorization(
            String requestId,
            String productCode,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt,
            boolean isApproved,
            String requestReason,
            String submissionAgencyName,
            AuthorizationStatus authorizationStatus,
            String productDescriptionFileName,
            String termsAndConditionsFileName,
            String rateScheduleFileName,
            String productEvidenceFileName,
            String revisionRequest,
            LocalDateTime updatedAt
    ) {
        this.requestId = requestId;
        this.productCode = productCode;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.isApproved = isApproved;
        this.requestReason = requestReason;
        this.submissionAgencyName = submissionAgencyName;
        this.authorizationStatus = authorizationStatus;
        this.productDescriptionFileName = productDescriptionFileName;
        this.termsAndConditionsFileName = termsAndConditionsFileName;
        this.rateScheduleFileName = rateScheduleFileName;
        this.productEvidenceFileName = productEvidenceFileName;
        this.revisionRequest = revisionRequest;
        this.updatedAt = updatedAt;
    }

    public void applyAuthorizationResult() {}

    public void cancelAuthorizationRequest() {}

    public void sendAuthorizationRequest() {}

    public void updateProductStatus() {}

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public boolean getIsApproved() { return isApproved; }
    public void setIsApproved(boolean approved) { isApproved = approved; }

    public String getRequestReason() { return requestReason; }
    public void setRequestReason(String requestReason) { this.requestReason = requestReason; }

    public String getSubmissionAgencyName() { return submissionAgencyName; }
    public void setSubmissionAgencyName(String submissionAgencyName) { this.submissionAgencyName = submissionAgencyName; }

    public AuthorizationStatus getAuthorizationStatus() { return authorizationStatus; }
    public void setAuthorizationStatus(AuthorizationStatus authorizationStatus) { this.authorizationStatus = authorizationStatus; }

    public String getProductDescriptionFileName() { return productDescriptionFileName; }
    public void setProductDescriptionFileName(String productDescriptionFileName) { this.productDescriptionFileName = productDescriptionFileName; }

    public String getTermsAndConditionsFileName() { return termsAndConditionsFileName; }
    public void setTermsAndConditionsFileName(String termsAndConditionsFileName) { this.termsAndConditionsFileName = termsAndConditionsFileName; }

    public String getRateScheduleFileName() { return rateScheduleFileName; }
    public void setRateScheduleFileName(String rateScheduleFileName) { this.rateScheduleFileName = rateScheduleFileName; }

    public String getProductEvidenceFileName() { return productEvidenceFileName; }
    public void setProductEvidenceFileName(String productEvidenceFileName) { this.productEvidenceFileName = productEvidenceFileName; }

    public String getRevisionRequest() { return revisionRequest; }
    public void setRevisionRequest(String revisionRequest) { this.revisionRequest = revisionRequest; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
