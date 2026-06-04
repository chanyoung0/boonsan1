package com.boonsan.domain.accident.dto;

import com.boonsan.domain.enums.AccidentReportStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DamageInvestigationResultResponse {

    private String accidentNumber;
    private String investigationId;
    private String adjusterId;
    private LocalDateTime investigationAt;
    private BigDecimal medicalExpense;
    private BigDecimal lostIncome;
    private BigDecimal repairCost;
    private BigDecimal settlementAmount;
    private Float faultRatio;
    private BigDecimal totalDamageAmount;
    private BigDecimal calculatedPaymentAmount;
    private String documentId;
    private String documentType;
    private String submissionStatus;
    private String faultRatioOpinion;
    private String adjusterOpinion;
    private String employeeNo;
    private AccidentReportStatus accidentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getInvestigationId() { return investigationId; }

    public void setInvestigationId(String investigationId) { this.investigationId = investigationId; }

    public String getAdjusterId() { return adjusterId; }

    public void setAdjusterId(String adjusterId) { this.adjusterId = adjusterId; }

    public LocalDateTime getInvestigationAt() { return investigationAt; }

    public void setInvestigationAt(LocalDateTime investigationAt) { this.investigationAt = investigationAt; }

    public BigDecimal getMedicalExpense() { return medicalExpense; }

    public void setMedicalExpense(BigDecimal medicalExpense) { this.medicalExpense = medicalExpense; }

    public BigDecimal getLostIncome() { return lostIncome; }

    public void setLostIncome(BigDecimal lostIncome) { this.lostIncome = lostIncome; }

    public BigDecimal getRepairCost() { return repairCost; }

    public void setRepairCost(BigDecimal repairCost) { this.repairCost = repairCost; }

    public BigDecimal getSettlementAmount() { return settlementAmount; }

    public void setSettlementAmount(BigDecimal settlementAmount) { this.settlementAmount = settlementAmount; }

    public Float getFaultRatio() { return faultRatio; }

    public void setFaultRatio(Float faultRatio) { this.faultRatio = faultRatio; }

    public BigDecimal getTotalDamageAmount() { return totalDamageAmount; }

    public void setTotalDamageAmount(BigDecimal totalDamageAmount) { this.totalDamageAmount = totalDamageAmount; }

    public BigDecimal getCalculatedPaymentAmount() { return calculatedPaymentAmount; }

    public void setCalculatedPaymentAmount(BigDecimal calculatedPaymentAmount) {
        this.calculatedPaymentAmount = calculatedPaymentAmount;
    }

    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getDocumentType() { return documentType; }

    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getSubmissionStatus() { return submissionStatus; }

    public void setSubmissionStatus(String submissionStatus) { this.submissionStatus = submissionStatus; }

    public String getFaultRatioOpinion() { return faultRatioOpinion; }

    public void setFaultRatioOpinion(String faultRatioOpinion) { this.faultRatioOpinion = faultRatioOpinion; }

    public String getAdjusterOpinion() { return adjusterOpinion; }

    public void setAdjusterOpinion(String adjusterOpinion) { this.adjusterOpinion = adjusterOpinion; }

    public String getEmployeeNo() { return employeeNo; }

    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public AccidentReportStatus getAccidentStatus() { return accidentStatus; }

    public void setAccidentStatus(AccidentReportStatus accidentStatus) { this.accidentStatus = accidentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }

    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
