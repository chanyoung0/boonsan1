package model.underwriting;

import enums.UnderwritingStatus;
import enums.UnderwritingTerm;
import enums.UnderwritingType;

import java.time.LocalDateTime;

// 언더라이팅 도메인 모델 — 청약 위험도 평가 및 인수 심사 정보 관리
public class Underwriting {

    private String underwritingId;
    private String deductionReason;
    private boolean isCoinsuranceRecommended;
    private String itemizedScores;
    private float totalScore;
    private String underwriter;
    private UnderwritingTerm underwritingItem;
    private String underwritingOpinion;
    private UnderwritingStatus underwritingStatus;
    private UnderwritingType underwritingType;
    private LocalDateTime underwrittenAt;

    private Coinsurance coinsurance;
    private UnderwritingResult underwritingResult;

    public Underwriting() {}

    public Underwriting(String underwriter, UnderwritingType underwritingType,
                        UnderwritingStatus underwritingStatus, UnderwritingTerm underwritingItem,
                        LocalDateTime underwrittenAt) {
        this.underwriter = underwriter;
        this.underwritingType = underwritingType;
        this.underwritingStatus = underwritingStatus;
        this.underwritingItem = underwritingItem;
        this.underwrittenAt = underwrittenAt;
    }

    // 심사 점수 계산 — 총점 반환
    public float calculateScore() { return totalScore; }

    // 자동 심사 실행
    public void executeAutoUnderwriting() {}

    // 심사 보고서 출력
    public String printReport() {
        return "UnderwritingReport{underwriter='" + underwriter
                + "', totalScore=" + totalScore
                + ", underwritingStatus=" + underwritingStatus
                + ", underwritingType=" + underwritingType
                + ", underwritingOpinion='" + underwritingOpinion
                + "', coinsuranceRecommended=" + isCoinsuranceRecommended
                + ", underwritingResult=" + underwritingResult + "}";
    }

    // 수동 심사 등록
    public void registerManualUnderwriting() {}

    // 임시 저장
    public void tempSave() {}

    public String             getUnderwritingId()                       { return underwritingId; }
    public void               setUnderwritingId(String v)              { this.underwritingId = v; }
    public String             getDeductionReason()                      { return deductionReason; }
    public void               setDeductionReason(String v)              { this.deductionReason = v; }
    public boolean            isCoinsuranceRecommended()                { return isCoinsuranceRecommended; }
    public void               setCoinsuranceRecommended(boolean v)      { this.isCoinsuranceRecommended = v; }
    public String             getItemizedScores()                       { return itemizedScores; }
    public void               setItemizedScores(String v)               { this.itemizedScores = v; }
    public float              getTotalScore()                           { return totalScore; }
    public void               setTotalScore(float v)                    { this.totalScore = v; }
    public String             getUnderwriter()                          { return underwriter; }
    public void               setUnderwriter(String v)                  { this.underwriter = v; }
    public UnderwritingTerm   getUnderwritingItem()                     { return underwritingItem; }
    public void               setUnderwritingItem(UnderwritingTerm v)   { this.underwritingItem = v; }
    public String             getUnderwritingOpinion()                  { return underwritingOpinion; }
    public void               setUnderwritingOpinion(String v)          { this.underwritingOpinion = v; }
    public UnderwritingStatus getUnderwritingStatus()                   { return underwritingStatus; }
    public void               setUnderwritingStatus(UnderwritingStatus v){ this.underwritingStatus = v; }
    public UnderwritingType   getUnderwritingType()                     { return underwritingType; }
    public void               setUnderwritingType(UnderwritingType v)   { this.underwritingType = v; }
    public LocalDateTime      getUnderwrittenAt()                       { return underwrittenAt; }
    public void               setUnderwrittenAt(LocalDateTime v)        { this.underwrittenAt = v; }
    public Coinsurance        getCoinsurance()                          { return coinsurance; }
    public void               setCoinsurance(Coinsurance v)             { this.coinsurance = v; }
    public UnderwritingResult getUnderwritingResult()                   { return underwritingResult; }
    public void               setUnderwritingResult(UnderwritingResult v){ this.underwritingResult = v; }

    @Override
    public String toString() {
        return "Underwriting{underwriter='" + underwriter + "', totalScore=" + totalScore + ", underwritingStatus=" + underwritingStatus + "}";
    }
}
