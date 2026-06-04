package claim.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ObjectionResponse {

    private String objectionId;
    private String accidentNumber;
    private String accidentStatus;
    private String documentId;
    private String paymentStatus;
    private BigDecimal finalPaymentAmount;
    private String claimantName;
    private String claimantPhone;
    private String objectionReason;
    private String requestedAction;
    private String employeeNo;
    private String objectionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime completedAt;

    public String getObjectionId() { return objectionId; }

    public void setObjectionId(String objectionId) { this.objectionId = objectionId; }

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getAccidentStatus() { return accidentStatus; }

    public void setAccidentStatus(String accidentStatus) { this.accidentStatus = accidentStatus; }

    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getPaymentStatus() { return paymentStatus; }

    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public BigDecimal getFinalPaymentAmount() { return finalPaymentAmount; }

    public void setFinalPaymentAmount(BigDecimal finalPaymentAmount) {
        this.finalPaymentAmount = finalPaymentAmount;
    }

    public String getClaimantName() { return claimantName; }

    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimantPhone() { return claimantPhone; }

    public void setClaimantPhone(String claimantPhone) { this.claimantPhone = claimantPhone; }

    public String getObjectionReason() { return objectionReason; }

    public void setObjectionReason(String objectionReason) { this.objectionReason = objectionReason; }

    public String getRequestedAction() { return requestedAction; }

    public void setRequestedAction(String requestedAction) { this.requestedAction = requestedAction; }

    public String getEmployeeNo() { return employeeNo; }

    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getObjectionStatus() { return objectionStatus; }

    public void setObjectionStatus(String objectionStatus) { this.objectionStatus = objectionStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }

    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }

    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
