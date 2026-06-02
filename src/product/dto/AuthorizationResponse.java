package product.dto;

import enums.AuthorizationStatus;
import enums.ProductStatus;
import model.insurance.Authorization;

import java.time.LocalDateTime;

public class AuthorizationResponse {

    private final String requestId;
    private final String productCode;
    private final LocalDateTime requestedAt;
    private final LocalDateTime approvedAt;
    private final boolean isApproved;
    private final String requestReason;
    private final String submissionAgencyName;
    private final AuthorizationStatus authorizationStatus;
    private final ProductStatus productStatus;
    private final String productDescriptionFileName;
    private final String termsAndConditionsFileName;
    private final String rateScheduleFileName;
    private final String productEvidenceFileName;
    private final String revisionRequest;
    private final LocalDateTime updatedAt;

    private AuthorizationResponse(
            String requestId,
            String productCode,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt,
            boolean isApproved,
            String requestReason,
            String submissionAgencyName,
            AuthorizationStatus authorizationStatus,
            ProductStatus productStatus,
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
        this.productStatus = productStatus;
        this.productDescriptionFileName = productDescriptionFileName;
        this.termsAndConditionsFileName = termsAndConditionsFileName;
        this.rateScheduleFileName = rateScheduleFileName;
        this.productEvidenceFileName = productEvidenceFileName;
        this.revisionRequest = revisionRequest;
        this.updatedAt = updatedAt;
    }

    public static AuthorizationResponse from(Authorization authorization, ProductStatus productStatus) {
        return new AuthorizationResponse(
                authorization.getRequestId(),
                authorization.getProductCode(),
                authorization.getRequestedAt(),
                authorization.getApprovedAt(),
                authorization.getIsApproved(),
                authorization.getRequestReason(),
                authorization.getSubmissionAgencyName(),
                authorization.getAuthorizationStatus(),
                productStatus,
                authorization.getProductDescriptionFileName(),
                authorization.getTermsAndConditionsFileName(),
                authorization.getRateScheduleFileName(),
                authorization.getProductEvidenceFileName(),
                authorization.getRevisionRequest(),
                authorization.getUpdatedAt()
        );
    }

    public String getRequestId() { return requestId; }
    public String getProductCode() { return productCode; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public boolean getIsApproved() { return isApproved; }
    public String getRequestReason() { return requestReason; }
    public String getSubmissionAgencyName() { return submissionAgencyName; }
    public AuthorizationStatus getAuthorizationStatus() { return authorizationStatus; }
    public ProductStatus getProductStatus() { return productStatus; }
    public String getProductDescriptionFileName() { return productDescriptionFileName; }
    public String getTermsAndConditionsFileName() { return termsAndConditionsFileName; }
    public String getRateScheduleFileName() { return rateScheduleFileName; }
    public String getProductEvidenceFileName() { return productEvidenceFileName; }
    public String getRevisionRequest() { return revisionRequest; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
