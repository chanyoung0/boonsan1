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

    public UnderwritingResult(UnderwritingResultType underwritingResult, String rejectionReason,
                              SurchargeCondition surchargeCondition, LocalDateTime confirmedAt) {
        this.underwritingResult = underwritingResult;
        this.rejectionReason = rejectionReason;
        this.surchargeCondition = surchargeCondition;
        this.confirmedAt = confirmedAt;
    }

    // 심사 결과 조회
    public void getResult() {
        if (underwritingResult == null)
            throw new IllegalStateException("심사 결과가 등록되지 않았습니다.");
    }

    // 심사 결과 등록
    public void registerResult() {
        if (underwritingResult == null)
            throw new IllegalArgumentException("심사 결과 유형이 필요합니다.");
        if (this.confirmedAt == null) this.confirmedAt = LocalDateTime.now();
    }

    public LocalDateTime        getConfirmedAt()                    { return confirmedAt; }
    public void                 setConfirmedAt(LocalDateTime v)     { this.confirmedAt = v; }
    public String               getRejectionReason()                { return rejectionReason; }
    public void                 setRejectionReason(String v)        { this.rejectionReason = v; }
    public SurchargeCondition   getSurchargeCondition()             { return surchargeCondition; }
    public void                 setSurchargeCondition(SurchargeCondition v) { this.surchargeCondition = v; }
    public UnderwritingResultType getUnderwritingResult()           { return underwritingResult; }
    public void                 setUnderwritingResult(UnderwritingResultType v) { this.underwritingResult = v; }

    @Override
    public String toString() {
        return "UnderwritingResult{underwritingResult=" + underwritingResult + ", confirmedAt=" + confirmedAt + "}";
    }
}
