package model.contract;

import enums.CompensationStatus;
import enums.EvaluationResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// 보상 평가 도메인 모델 — 보험금 지급 통계 및 손해 분석 정보 관리
public class CompensationEvaluation {

    private String evaluationId;
    private int evaluationMonth;
    private String submissionAgencyName;
    private BigDecimal damageAmount;
    private String damageAnalysisResult;
    private String compensationStatistics;
    private EvaluationResult evaluationResult;
    private CompensationStatus evaluationStatus;
    private final List<PaymentCollection> paymentCollections = new ArrayList<>();
    private final List<Transfer> transfers = new ArrayList<>();

    public CompensationEvaluation() {}

    // 보상 평가 기본 정보로 초기화
    public CompensationEvaluation(String evaluationId, int evaluationMonth, String submissionAgencyName) {
        this.evaluationId = evaluationId;
        this.evaluationMonth = evaluationMonth;
        this.submissionAgencyName = submissionAgencyName;
        this.evaluationStatus = CompensationStatus.IN_PROGRESS;
    }

    // 손해액 분석 — 분석 요약 텍스트 반환
    public String analyzeDamageAmount() {
        return damageAnalysisResult != null ? damageAnalysisResult : "분석 결과 없음";
    }

    // 보상 통계 산출
    public void calculateCompensationStatistics() {}

    // 평가 결과 저장
    public void saveEvaluationResult() {}

    public String getEvaluationId() { return evaluationId; }
    public int getEvaluationMonth() { return evaluationMonth; }
    public String getSubmissionAgencyName() { return submissionAgencyName; }
    public BigDecimal getDamageAmount() { return damageAmount; }
    public String getDamageAnalysisResult() { return damageAnalysisResult; }
    public String getCompensationStatistics() { return compensationStatistics; }
    public EvaluationResult getEvaluationResult() { return evaluationResult; }
    public CompensationStatus getEvaluationStatus() { return evaluationStatus; }
    public List<PaymentCollection> getPaymentCollections() { return paymentCollections; }
    public List<Transfer> getTransfers() { return transfers; }

    public void setEvaluationId(String s) { this.evaluationId = s; }
    public void setEvaluationMonth(int v) { this.evaluationMonth = v; }
    public void setSubmissionAgencyName(String s) { this.submissionAgencyName = s; }
    public void setDamageAmount(BigDecimal v) { this.damageAmount = v; }
    public void setDamageAnalysisResult(String s) { this.damageAnalysisResult = s; }
    public void setCompensationStatistics(String s) { this.compensationStatistics = s; }
    public void setEvaluationResult(EvaluationResult r) { this.evaluationResult = r; }
    public void setEvaluationStatus(CompensationStatus s) { this.evaluationStatus = s; }
    public void addPaymentCollection(PaymentCollection p) { this.paymentCollections.add(p); }
    public void addTransfer(Transfer t) { this.transfers.add(t); }

    @Override
    public String toString() {
        return "CompensationEvaluation{id='" + evaluationId + "', month=" + evaluationMonth
                + ", status=" + evaluationStatus + "}";
    }
}
