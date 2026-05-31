package model.underwriting;

import enums.RejectionReason;
import enums.RequestReason;
import enums.RequestStatus;
import enums.SurchargeCondition;
import enums.UnderwritingResultType;
import enums.UnderwritingType;

import java.time.LocalDateTime;

// 언더라이팅 요청 도메인 모델 — 배서/부활 심사 요청 정보 관리
public class UnderwritingRequest {

    private String requestId;
    private String underwritingId;
    private LocalDateTime appliedAt;
    private RejectionReason rejectionReason;
    private RequestReason requestReason;
    private RequestStatus requestStatus;
    private SurchargeCondition surchargeCondition;
    private UnderwritingResultType underwritingResult;
    private UnderwritingType underwritingType;

    public UnderwritingRequest() {}

    public UnderwritingRequest(LocalDateTime appliedAt, RequestReason requestReason,
                               UnderwritingType underwritingType, RequestStatus requestStatus) {
        this.appliedAt = appliedAt;
        this.requestReason = requestReason;
        this.underwritingType = underwritingType;
        this.requestStatus = requestStatus;
    }

    // U/W 요청 상태 변경
    public void changeUWStatus() {}

    // U/W 결과 등록
    public void registerUWResult() {}

    // 언더라이팅 요청
    public void requestUnderwriting() {}

    public String               getRequestId()                          { return requestId; }
    public void                 setRequestId(String v)                  { this.requestId = v; }
    public String               getUnderwritingId()                     { return underwritingId; }
    public void                 setUnderwritingId(String v)             { this.underwritingId = v; }
    public LocalDateTime        getAppliedAt()                          { return appliedAt; }
    public void                 setAppliedAt(LocalDateTime v)           { this.appliedAt = v; }
    public RejectionReason      getRejectionReason()                    { return rejectionReason; }
    public void                 setRejectionReason(RejectionReason v)   { this.rejectionReason = v; }
    public RequestReason        getRequestReason()                      { return requestReason; }
    public void                 setRequestReason(RequestReason v)       { this.requestReason = v; }
    public RequestStatus        getRequestStatus()                      { return requestStatus; }
    public void                 setRequestStatus(RequestStatus v)       { this.requestStatus = v; }
    public SurchargeCondition   getSurchargeCondition()                 { return surchargeCondition; }
    public void                 setSurchargeCondition(SurchargeCondition v){ this.surchargeCondition = v; }
    public UnderwritingResultType getUnderwritingResult()               { return underwritingResult; }
    public void                 setUnderwritingResult(UnderwritingResultType v){ this.underwritingResult = v; }
    public UnderwritingType     getUnderwritingType()                   { return underwritingType; }
    public void                 setUnderwritingType(UnderwritingType v) { this.underwritingType = v; }

    @Override
    public String toString() {
        return "UnderwritingRequest{requestStatus=" + requestStatus + ", underwritingType=" + underwritingType + "}";
    }
}
