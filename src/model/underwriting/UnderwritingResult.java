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

    // DB 전환을 위해 추가된 필드 — 행 식별자 + 어떤 Underwriting 의 결과인지.
    private String resultId;
    private String underwritingId;

    public UnderwritingResult() {}

    public UnderwritingResult(UnderwritingResultType underwritingResult, String rejectionReason,
                              SurchargeCondition surchargeCondition, LocalDateTime confirmedAt) {
        this.underwritingResult = underwritingResult;
        this.rejectionReason = rejectionReason;
        this.surchargeCondition = surchargeCondition;
        this.confirmedAt = confirmedAt;
    }

    // 심사 결과 조회
    public void getResult() {}

    // 심사 결과 등록
    public void registerResult() {}

    public LocalDateTime        getConfirmedAt()                    { return confirmedAt; }
    public void                 setConfirmedAt(LocalDateTime v)     { this.confirmedAt = v; }
    public String               getRejectionReason()                { return rejectionReason; }
    public void                 setRejectionReason(String v)        { this.rejectionReason = v; }
    public SurchargeCondition   getSurchargeCondition()             { return surchargeCondition; }
    public void                 setSurchargeCondition(SurchargeCondition v) { this.surchargeCondition = v; }
    public UnderwritingResultType getUnderwritingResult()           { return underwritingResult; }
    public void                 setUnderwritingResult(UnderwritingResultType v) { this.underwritingResult = v; }
    public String               getResultId()                       { return resultId; }
    public void                 setResultId(String v)               { this.resultId = v; }
    public String               getUnderwritingId()                 { return underwritingId; }
    public void                 setUnderwritingId(String v)         { this.underwritingId = v; }

    @Override
    public String toString() {
        return "UnderwritingResult{underwritingResult=" + underwritingResult + ", confirmedAt=" + confirmedAt + "}";
    }
}
