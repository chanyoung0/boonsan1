package com.boonsan.domain.accident.dto;

import com.boonsan.domain.enums.AccidentReportStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentApprovalDocumentResponse {

    private String documentId;
    private String accidentNumber;
    private String investigationId;
    private String documentType;
    private String submissionStatus;
    private BigDecimal totalDamageAmount;
    private Float faultRatio;
    private BigDecimal calculatedPaymentAmount;
    private String faultRatioOpinion;
    private String adjusterOpinion;
    private String employeeNo;
    private AccidentReportStatus accidentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;

    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getInvestigationId() { return investigationId; }

    public void setInvestigationId(String investigationId) { this.investigationId = investigationId; }

    public String getDocumentType() { return documentType; }

    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getSubmissionStatus() { return submissionStatus; }

    public void setSubmissionStatus(String submissionStatus) { this.submissionStatus = submissionStatus; }

    public BigDecimal getTotalDamageAmount() { return totalDamageAmount; }

    public void setTotalDamageAmount(BigDecimal totalDamageAmount) { this.totalDamageAmount = totalDamageAmount; }

    public Float getFaultRatio() { return faultRatio; }

    public void setFaultRatio(Float faultRatio) { this.faultRatio = faultRatio; }

    public BigDecimal getCalculatedPaymentAmount() { return calculatedPaymentAmount; }

    public void setCalculatedPaymentAmount(BigDecimal calculatedPaymentAmount) {
        this.calculatedPaymentAmount = calculatedPaymentAmount;
    }

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
