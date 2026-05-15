package service.compensation;

import db.CompensationEvaluationDBO;
import enums.CompensationStatus;
import enums.EvaluationResult;
import model.contract.CompensationEvaluation;

import java.math.BigDecimal;
import java.time.LocalDate;

// 보상 평가 서비스 — 손해액 분석, 마감 통계 산출, 평가 결과 저장 유스케이스 흐름 담당
public class CompensationEvaluationService {

    // CompensationEvaluation 객체 생성 및 저장
    public void saveEvaluation(String period, String insuranceType) {
        String evalId = "EVAL-" + System.currentTimeMillis();
        int    month  = LocalDate.now().getMonthValue();
        CompensationEvaluation evaluation = new CompensationEvaluation(
            evalId, month, CompensationStatus.COMPLETED, EvaluationResult.GOOD, "자사"
        );
        evaluation.setDamageAmount(new BigDecimal("125000000"));
        evaluation.setDamageAnalysisResult("전월 대비 손해액 2.3% 증가");
        evaluation.setCompensationStatistics("접수 324건 / 처리 298건 / 미결 26건 / 지급보험금 합계 1,250,000,000원");
        new CompensationEvaluationDBO().save(evaluation);
    }
}
