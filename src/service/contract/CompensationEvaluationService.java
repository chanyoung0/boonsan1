package service.contract;

import enums.CompensationStatus;
import enums.EvaluationResult;
import model.contract.CompensationEvaluation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompensationEvaluationService {

    private static final Map<String, CompensationEvaluation> evaluationMap = new LinkedHashMap<>();

    public static CompensationEvaluation createEvaluation(int evaluationMonth, String submissionAgencyName,
                                                          BigDecimal damageAmount) {
        String evaluationId = generateEvaluationId();
        CompensationEvaluation evaluation = new CompensationEvaluation(
                evaluationId,
                evaluationMonth,
                CompensationStatus.IN_PROGRESS,
                null,
                submissionAgencyName
        );
        evaluation.setDamageAmount(damageAmount);
        evaluation.setDamageAnalysisResult(analyzeDamageAmount(damageAmount));
        evaluation.setCompensationStatistics(
                createCompensationStatistics(evaluationMonth, submissionAgencyName, damageAmount)
        );
        evaluation.calculateCompensationStatistics();
        evaluationMap.put(evaluationId, evaluation);
        return evaluation;
    }

    public static String analyzeDamageAmount(BigDecimal damageAmount) {
        if (damageAmount == null) {
            return "손해액 정보가 없어 분석을 보류합니다.";
        }
        if (damageAmount.compareTo(BigDecimal.valueOf(1_000_000L)) < 0) {
            return "소액 손해 구간입니다.";
        }
        if (damageAmount.compareTo(BigDecimal.valueOf(10_000_000L)) < 0) {
            return "일반 손해 구간입니다.";
        }
        return "고액 손해 구간입니다.";
    }

    public static String createCompensationStatistics(int evaluationMonth, String submissionAgencyName,
                                                      BigDecimal damageAmount) {
        return "평가월: " + evaluationMonth + "월"
                + " | 제출기관: " + submissionAgencyName
                + " | 손해액: " + damageAmount;
    }

    public static CompensationEvaluation completeEvaluation(String evaluationId, EvaluationResult result) {
        CompensationEvaluation evaluation = findEvaluationById(evaluationId);
        if (evaluation == null || evaluation.getEvaluationStatus() == CompensationStatus.CLOSED) {
            return null;
        }
        evaluation.setEvaluationResult(result);
        evaluation.saveEvaluationResult();
        return evaluation;
    }

    public static CompensationEvaluation closeEvaluation(String evaluationId) {
        CompensationEvaluation evaluation = findEvaluationById(evaluationId);
        if (evaluation == null) {
            return null;
        }
        evaluation.setEvaluationStatus(CompensationStatus.CLOSED);
        return evaluation;
    }

    public static List<CompensationEvaluation> getEvaluationList() {
        return new ArrayList<>(evaluationMap.values());
    }

    public static CompensationEvaluation findEvaluationById(String evaluationId) {
        return evaluationMap.get(evaluationId);
    }

    private static String generateEvaluationId() {
        return "CE-" + System.currentTimeMillis();
    }
}
