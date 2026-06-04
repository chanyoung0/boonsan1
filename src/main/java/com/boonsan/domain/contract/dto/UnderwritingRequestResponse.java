package com.boonsan.domain.contract.dto;

import com.boonsan.domain.enums.RejectionReason;
import com.boonsan.domain.enums.RequestReason;
import com.boonsan.domain.enums.RequestStatus;
import com.boonsan.domain.enums.SurchargeCondition;
import com.boonsan.domain.enums.UnderwritingResultType;
import com.boonsan.domain.enums.UnderwritingType;

import java.time.LocalDateTime;

public class UnderwritingRequestResponse {

    private String requestId;
    private String policyNumber;
    private RequestReason requestReason;
    private String sourceId;
    private UnderwritingType underwritingType;
    private RequestStatus requestStatus;
    private UnderwritingResultType underwritingResult;
    private RejectionReason rejectionReason;
    private SurchargeCondition surchargeCondition;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public RequestReason getRequestReason() { return requestReason; }
    public void setRequestReason(RequestReason requestReason) { this.requestReason = requestReason; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public UnderwritingType getUnderwritingType() { return underwritingType; }
    public void setUnderwritingType(UnderwritingType underwritingType) { this.underwritingType = underwritingType; }

    public RequestStatus getRequestStatus() { return requestStatus; }
    public void setRequestStatus(RequestStatus requestStatus) { this.requestStatus = requestStatus; }

    public UnderwritingResultType getUnderwritingResult() { return underwritingResult; }
    public void setUnderwritingResult(UnderwritingResultType underwritingResult) {
        this.underwritingResult = underwritingResult;
    }

    public RejectionReason getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(RejectionReason rejectionReason) { this.rejectionReason = rejectionReason; }

    public SurchargeCondition getSurchargeCondition() { return surchargeCondition; }
    public void setSurchargeCondition(SurchargeCondition surchargeCondition) {
        this.surchargeCondition = surchargeCondition;
    }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
