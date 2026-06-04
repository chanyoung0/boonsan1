package com.boonsan.domain.contract.dto;

import com.boonsan.domain.enums.ChangeReason;
import com.boonsan.domain.enums.EndorsementStatus;
import com.boonsan.domain.enums.EndorsementType;

import java.time.LocalDateTime;

public class EndorsementResponse {

    private String endorsementId;
    private String policyNumber;
    private EndorsementType endorsementType;
    private ChangeReason changeReason;
    private String previousContent;
    private String newContent;
    private EndorsementStatus endorsementStatus;
    private String underwritingRequestId;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime cancelledAt;

    public String getEndorsementId() { return endorsementId; }
    public void setEndorsementId(String endorsementId) { this.endorsementId = endorsementId; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public EndorsementType getEndorsementType() { return endorsementType; }
    public void setEndorsementType(EndorsementType endorsementType) { this.endorsementType = endorsementType; }

    public ChangeReason getChangeReason() { return changeReason; }
    public void setChangeReason(ChangeReason changeReason) { this.changeReason = changeReason; }

    public String getPreviousContent() { return previousContent; }
    public void setPreviousContent(String previousContent) { this.previousContent = previousContent; }

    public String getNewContent() { return newContent; }
    public void setNewContent(String newContent) { this.newContent = newContent; }

    public EndorsementStatus getEndorsementStatus() { return endorsementStatus; }
    public void setEndorsementStatus(EndorsementStatus endorsementStatus) {
        this.endorsementStatus = endorsementStatus;
    }

    public String getUnderwritingRequestId() { return underwritingRequestId; }
    public void setUnderwritingRequestId(String underwritingRequestId) {
        this.underwritingRequestId = underwritingRequestId;
    }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
