package model.contract;

import enums.CompensationStatus;

import java.math.BigDecimal;

public class CompensationEvaluation {

    private String compensationStatistics;
    private BigDecimal damageAmount;
    private String damageAnalysisResult;
    private String evaluationId;
    private int evaluationMonth;
    private String submissionAgencyName;
    private CompensationStatus status;

    public void analyzeDamageAmount() {}

    public void calculateCompensationStatistics() {}

    public void saveEvaluationResult() {}
}
