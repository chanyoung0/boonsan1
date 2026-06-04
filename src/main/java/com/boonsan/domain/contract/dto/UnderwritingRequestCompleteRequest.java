package com.boonsan.domain.contract.dto;

import com.boonsan.domain.enums.RejectionReason;
import com.boonsan.domain.enums.SurchargeCondition;
import com.boonsan.domain.enums.UnderwritingResultType;
import jakarta.validation.constraints.NotNull;

public class UnderwritingRequestCompleteRequest {

    @NotNull
    private UnderwritingResultType underwritingResult;

    private SurchargeCondition surchargeCondition;

    private RejectionReason rejectionReason;

    public UnderwritingResultType getUnderwritingResult() { return underwritingResult; }
    public void setUnderwritingResult(UnderwritingResultType underwritingResult) {
        this.underwritingResult = underwritingResult;
    }

    public SurchargeCondition getSurchargeCondition() { return surchargeCondition; }
    public void setSurchargeCondition(SurchargeCondition surchargeCondition) {
        this.surchargeCondition = surchargeCondition;
    }

    public RejectionReason getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(RejectionReason rejectionReason) { this.rejectionReason = rejectionReason; }
}
