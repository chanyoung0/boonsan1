package com.boonsan.domain.model.contract;

import com.boonsan.domain.enums.CompensationStatus;
import com.boonsan.domain.enums.EvaluationResult;

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

    public void analyzeDamageAmount() {}

    public void calculateCompensationStatistics() {}

    public void saveEvaluationResult() {}
}
