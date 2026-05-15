package model.underwriting;

import enums.UnderwritingStatus;
import enums.UnderwritingTerm;
import enums.UnderwritingType;

import java.time.LocalDateTime;

// 언더라이팅 도메인 모델 — 청약 위험도 평가 및 인수 심사 정보 관리
public class Underwriting {

    private float totalScore;
    private String underwriter;
    private UnderwritingTerm underwritingItem;
    private UnderwritingStatus underwritingStatus;
    private UnderwritingType underwritingType;
    private String underwritingOpinion;
    private String itemizedScores;
    private String deductionReason;
    private boolean isCoinsuranceRecommended;
    private LocalDateTime underwrittenAt;
    private Coinsurance coinsurance;
    private UnderwritingResult underwritingResult;

    public Underwriting() {}

    // 심사 시작 시점/유형으로 초기화
    public Underwriting(UnderwritingType type, String underwriter, LocalDateTime underwrittenAt) {
        this.underwritingType = type;
        this.underwriter = underwriter;
        this.underwrittenAt = underwrittenAt;
        this.totalScore = 100f;
    }

    // 심사 점수 계산 — 총점 반환
    public float calculateScore() {
        return totalScore;
    }

    // 자동 심사 실행
    public void executeAutoUnderwriting() {}

    // 심사 보고서 출력
    public void printReport() {}

    // 수동 심사 등록
    public void registerManualUnderwriting() {}

    // 임시 저장
    public void tempSave() {}

    public float getTotalScore() { return totalScore; }
    public String getUnderwriter() { return underwriter; }
    public UnderwritingTerm getUnderwritingItem() { return underwritingItem; }
    public UnderwritingStatus getUnderwritingStatus() { return underwritingStatus; }
    public UnderwritingType getUnderwritingType() { return underwritingType; }
    public String getUnderwritingOpinion() { return underwritingOpinion; }
    public String getItemizedScores() { return itemizedScores; }
    public String getDeductionReason() { return deductionReason; }
    public boolean isCoinsuranceRecommended() { return isCoinsuranceRecommended; }
    public LocalDateTime getUnderwrittenAt() { return underwrittenAt; }
    public Coinsurance getCoinsurance() { return coinsurance; }
    public UnderwritingResult getUnderwritingResult() { return underwritingResult; }

    public void setTotalScore(float v) { this.totalScore = v; }
    public void setUnderwriter(String s) { this.underwriter = s; }
    public void setUnderwritingItem(UnderwritingTerm t) { this.underwritingItem = t; }
    public void setUnderwritingStatus(UnderwritingStatus s) { this.underwritingStatus = s; }
    public void setUnderwritingType(UnderwritingType t) { this.underwritingType = t; }
    public void setUnderwritingOpinion(String s) { this.underwritingOpinion = s; }
    public void setItemizedScores(String s) { this.itemizedScores = s; }
    public void setDeductionReason(String s) { this.deductionReason = s; }
    public void setCoinsuranceRecommended(boolean b) { this.isCoinsuranceRecommended = b; }
    public void setUnderwrittenAt(LocalDateTime t) { this.underwrittenAt = t; }
    public void setCoinsurance(Coinsurance c) { this.coinsurance = c; }
    public void setUnderwritingResult(UnderwritingResult r) { this.underwritingResult = r; }

    @Override
    public String toString() {
        return "Underwriting{type=" + underwritingType + ", score=" + totalScore
                + ", status=" + underwritingStatus + "}";
    }
}
