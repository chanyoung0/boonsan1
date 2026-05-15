package model.underwriting;

import enums.SurchargeCondition;
import enums.UnderwritingResultType;

import java.time.LocalDateTime;

// 언더라이팅 결과 도메인 모델 — 심사 최종 결과 정보 관리
public class UnderwritingResult {

    private LocalDateTime confirmedAt;
    private String rejectionReason;
    private SurchargeCondition surchargeCondition;
    private UnderwritingResultType underwritingResult;

    public UnderwritingResult() {}

    // 심사 결과 기본 정보로 초기화
    public UnderwritingResult(LocalDateTime confirmedAt, UnderwritingResultType underwritingResult,
                              SurchargeCondition surchargeCondition, String rejectionReason) {
        this.confirmedAt = confirmedAt;
        this.underwritingResult = underwritingResult;
        this.surchargeCondition = surchargeCondition;
        this.rejectionReason = rejectionReason;
    }

    // 심사 결과 조회
    public void getResult() {}

    // 심사 결과 등록
    public void registerResult() {}

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public SurchargeCondition getSurchargeCondition() { return surchargeCondition; }
    public UnderwritingResultType getUnderwritingResult() { return underwritingResult; }

    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    public void setRejectionReason(String s) { this.rejectionReason = s; }
    public void setSurchargeCondition(SurchargeCondition c) { this.surchargeCondition = c; }
    public void setUnderwritingResult(UnderwritingResultType r) { this.underwritingResult = r; }

    @Override
    public String toString() {
        return "UnderwritingResult{result=" + underwritingResult + ", surcharge=" + surchargeCondition
                + ", confirmedAt=" + confirmedAt + "}";
    }
}
