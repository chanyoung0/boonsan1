package model.underwriting;

import enums.RejectionReason;
import enums.RequestReason;
import enums.RequestStatus;
import enums.SurchargeCondition;
import enums.UnderwritingResultType;
import enums.UnderwritingType;

import java.time.LocalDateTime;

public class UnderwritingRequest {

    private LocalDateTime appliedAt;
    private LocalDateTime appliedId;
    private RejectionReason rejectionReason;
    private RequestReason requestReason;
    private RequestStatus requestStatus;
    private SurchargeCondition surchargeCondition;
    private UnderwritingResultType underwritingResult;
    private UnderwritingType underwritingType;

    public void changeStatus() {}

    public void registerUWResult() {}

    public void renewalIntention() {}

    public void requestUnderwriting() {}
}
