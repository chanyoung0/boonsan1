package model.contract;

import enums.CompensationStatus;
import enums.EvaluationResult;

import java.math.BigDecimal;

// 보상 평가 도메인 모델 — 보험금 지급 통계 및 손해 분석 정보 관리
public class CompensationEvaluation {

    private String compensationStatistics;
    private BigDecimal damageAmount;
    private String damageAnalysisResult;
    private String evaluationId;
    private int evaluationMonth;
    private EvaluationResult evaluationResult;
    private CompensationStatus evaluationStatus;
    private String submissionAgencyName;

    private PaymentCollection paymentCollection;
    private Transfer transfer;

    public CompensationEvaluation() {}

    public CompensationEvaluation(String evaluationId, int evaluationMonth,
                                  CompensationStatus evaluationStatus, EvaluationResult evaluationResult,
                                  String submissionAgencyName) {
        this.evaluationId = evaluationId;
        this.evaluationMonth = evaluationMonth;
        this.evaluationStatus = evaluationStatus;
        this.evaluationResult = evaluationResult;
        this.submissionAgencyName = submissionAgencyName;
    }

    // 손해액 분석 결과 반환
    public String analyzeDamageAmount() { return damageAnalysisResult; }

    // 보상 통계 계산
    public void calculateCompensationStatistics() {
        if (damageAmount == null)
            throw new IllegalStateException("손해액이 설정되지 않았습니다.");
        this.compensationStatistics = "평가월: " + evaluationMonth
                + ", 손해액: " + damageAmount
                + ", 상태: " + evaluationStatus;
    }

    // 평가 결과 저장
    public void saveEvaluationResult() {
        if (evaluationId == null || evaluationId.isEmpty())
            throw new IllegalStateException("평가 ID가 없습니다.");
        if (evaluationResult == null)
            throw new IllegalStateException("평가 결과가 설정되지 않았습니다.");
        this.evaluationStatus = enums.CompensationStatus.COMPLETED;
    }

    public String             getCompensationStatistics()                    { return compensationStatistics; }
    public void               setCompensationStatistics(String v)           { this.compensationStatistics = v; }
    public BigDecimal         getDamageAmount()                             { return damageAmount; }
    public void               setDamageAmount(BigDecimal v)                 { this.damageAmount = v; }
    public String             getDamageAnalysisResult()                     { return damageAnalysisResult; }
    public void               setDamageAnalysisResult(String v)             { this.damageAnalysisResult = v; }
    public String             getEvaluationId()                             { return evaluationId; }
    public void               setEvaluationId(String v)                     { this.evaluationId = v; }
    public int                getEvaluationMonth()                          { return evaluationMonth; }
    public void               setEvaluationMonth(int v)                     { this.evaluationMonth = v; }
    public EvaluationResult   getEvaluationResult()                         { return evaluationResult; }
    public void               setEvaluationResult(EvaluationResult v)       { this.evaluationResult = v; }
    public CompensationStatus getEvaluationStatus()                         { return evaluationStatus; }
    public void               setEvaluationStatus(CompensationStatus v)     { this.evaluationStatus = v; }
    public String             getSubmissionAgencyName()                     { return submissionAgencyName; }
    public void               setSubmissionAgencyName(String v)             { this.submissionAgencyName = v; }
    public PaymentCollection  getPaymentCollection()                        { return paymentCollection; }
    public void               setPaymentCollection(PaymentCollection v)     { this.paymentCollection = v; }
    public Transfer           getTransfer()                                 { return transfer; }
    public void               setTransfer(Transfer v)                       { this.transfer = v; }

    @Override
    public String toString() {
        return "CompensationEvaluation{evaluationId='" + evaluationId + "', evaluationStatus=" + evaluationStatus + "}";
    }
}
