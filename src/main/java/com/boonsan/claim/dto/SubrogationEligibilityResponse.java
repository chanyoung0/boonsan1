package com.boonsan.claim.dto;

import java.math.BigDecimal;

public class SubrogationEligibilityResponse {

    private String accidentNumber;
    private String documentId;
    private String investigationId;
    private BigDecimal paidAmount;
    private String paymentStatus;
    private String accidentStatus;
    private boolean eligible;
    private String message;

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getInvestigationId() { return investigationId; }

    public void setInvestigationId(String investigationId) { this.investigationId = investigationId; }

    public BigDecimal getPaidAmount() { return paidAmount; }

    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public String getPaymentStatus() { return paymentStatus; }

    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getAccidentStatus() { return accidentStatus; }

    public void setAccidentStatus(String accidentStatus) { this.accidentStatus = accidentStatus; }

    public boolean isEligible() { return eligible; }

    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
