package com.boonsan.domain.accident.dto;

import java.math.BigDecimal;

public class PaymentApprovalDraftResponse {

    private final String accidentNumber;
    private final BigDecimal totalDamageAmount;
    private final Float faultRatio;
    private final BigDecimal calculatedPaymentAmount;
    private final BigDecimal medicalExpense;
    private final BigDecimal lostIncome;
    private final BigDecimal repairCost;
    private final BigDecimal settlementAmount;
    private final String draftMessage;

    public PaymentApprovalDraftResponse(
            String accidentNumber,
            BigDecimal totalDamageAmount,
            Float faultRatio,
            BigDecimal calculatedPaymentAmount,
            BigDecimal medicalExpense,
            BigDecimal lostIncome,
            BigDecimal repairCost,
            BigDecimal settlementAmount,
            String draftMessage
    ) {
        this.accidentNumber = accidentNumber;
        this.totalDamageAmount = totalDamageAmount;
        this.faultRatio = faultRatio;
        this.calculatedPaymentAmount = calculatedPaymentAmount;
        this.medicalExpense = medicalExpense;
        this.lostIncome = lostIncome;
        this.repairCost = repairCost;
        this.settlementAmount = settlementAmount;
        this.draftMessage = draftMessage;
    }

    public String getAccidentNumber() { return accidentNumber; }

    public BigDecimal getTotalDamageAmount() { return totalDamageAmount; }

    public Float getFaultRatio() { return faultRatio; }

    public BigDecimal getCalculatedPaymentAmount() { return calculatedPaymentAmount; }

    public BigDecimal getMedicalExpense() { return medicalExpense; }

    public BigDecimal getLostIncome() { return lostIncome; }

    public BigDecimal getRepairCost() { return repairCost; }

    public BigDecimal getSettlementAmount() { return settlementAmount; }

    public String getDraftMessage() { return draftMessage; }
}
