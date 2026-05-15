package model.underwriting;

import enums.RejectionReason;
import enums.RequestReason;
import enums.RequestStatus;
import enums.SurchargeCondition;
import enums.UnderwritingResultType;
import enums.UnderwritingType;

import java.time.LocalDateTime;

// 언더라이팅 요청 도메인 모델 — 배서/부활 등 심사 요청 정보 관리
public class UnderwritingRequest {

    private String appliedId;
    private LocalDateTime appliedAt;
    private RejectionReason rejectionReason;
    private RequestReason requestReason;
    private RequestStatus requestStatus;
    private SurchargeCondition surchargeCondition;
    private UnderwritingResultType underwritingResult;
    private UnderwritingType underwritingType;

    public UnderwritingRequest() {}

    // 심사 요청 기본 정보로 초기화
    public UnderwritingRequest(String appliedId, LocalDateTime appliedAt, RequestReason requestReason,
                               UnderwritingType underwritingType, RequestStatus requestStatus) {
        this.appliedId = appliedId;
        this.appliedAt = appliedAt;
        this.requestReason = requestReason;
        this.underwritingType = underwritingType;
        this.requestStatus = requestStatus;
    }

    // U/W 요청 상태 변경
    public void changeUWStatus() {}

    // U/W 결과 등록
    public void registerUWResult() {}

    // 언더라이팅 요청 발송
    public void requestUnderwriting() {}

    public String getAppliedId() { return appliedId; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public RejectionReason getRejectionReason() { return rejectionReason; }
    public RequestReason getRequestReason() { return requestReason; }
    public RequestStatus getRequestStatus() { return requestStatus; }
    public SurchargeCondition getSurchargeCondition() { return surchargeCondition; }
    public UnderwritingResultType getUnderwritingResult() { return underwritingResult; }
    public UnderwritingType getUnderwritingType() { return underwritingType; }

    public void setAppliedId(String s) { this.appliedId = s; }
    public void setAppliedAt(LocalDateTime t) { this.appliedAt = t; }
    public void setRejectionReason(RejectionReason r) { this.rejectionReason = r; }
    public void setRequestReason(RequestReason r) { this.requestReason = r; }
    public void setRequestStatus(RequestStatus r) { this.requestStatus = r; }
    public void setSurchargeCondition(SurchargeCondition c) { this.surchargeCondition = c; }
    public void setUnderwritingResult(UnderwritingResultType r) { this.underwritingResult = r; }
    public void setUnderwritingType(UnderwritingType t) { this.underwritingType = t; }

    @Override
    public String toString() {
        return "UnderwritingRequest{appliedId='" + appliedId + "', type=" + underwritingType
                + ", status=" + requestStatus + "}";
    }
}
