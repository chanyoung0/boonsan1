package com.boonsan.claim.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubrogationResponse {

    private String subrogationId;
    private String accidentNumber;
    private String documentId;
    private String investigationId;
    private String targetName;
    private String subrogationReason;
    private BigDecimal subrogationAmount;
    private String employeeNo;
    private String subrogationStatus;
    private BigDecimal paidAmount;
    private String paymentStatus;
    private String accidentStatus;
    private BigDecimal recoveredAmount;
    private LocalDateTime recoveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getSubrogationId() { return subrogationId; }

    public void setSubrogationId(String subrogationId) { this.subrogationId = subrogationId; }

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getInvestigationId() { return investigationId; }

    public void setInvestigationId(String investigationId) { this.investigationId = investigationId; }

    public String getTargetName() { return targetName; }

    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getSubrogationReason() { return subrogationReason; }

    public void setSubrogationReason(String subrogationReason) { this.subrogationReason = subrogationReason; }

    public BigDecimal getSubrogationAmount() { return subrogationAmount; }

    public void setSubrogationAmount(BigDecimal subrogationAmount) {
        this.subrogationAmount = subrogationAmount;
    }

    public String getEmployeeNo() { return employeeNo; }

    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getSubrogationStatus() { return subrogationStatus; }

    public void setSubrogationStatus(String subrogationStatus) {
        this.subrogationStatus = subrogationStatus;
    }

    public BigDecimal getPaidAmount() { return paidAmount; }

    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public String getPaymentStatus() { return paymentStatus; }

    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getAccidentStatus() { return accidentStatus; }

    public void setAccidentStatus(String accidentStatus) { this.accidentStatus = accidentStatus; }

    public BigDecimal getRecoveredAmount() { return recoveredAmount; }

    public void setRecoveredAmount(BigDecimal recoveredAmount) { this.recoveredAmount = recoveredAmount; }

    public LocalDateTime getRecoveredAt() { return recoveredAt; }

    public void setRecoveredAt(LocalDateTime recoveredAt) { this.recoveredAt = recoveredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
