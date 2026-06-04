package com.boonsan.claim.dto;

import java.math.BigDecimal;

public class ObjectionEligibilityResponse {

    private String accidentNumber;
    private String accidentStatus;
    private String documentId;
    private String paymentStatus;
    private BigDecimal finalPaymentAmount;
    private boolean eligible;
    private String unavailableReason;

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getAccidentStatus() { return accidentStatus; }

    public void setAccidentStatus(String accidentStatus) { this.accidentStatus = accidentStatus; }

    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getPaymentStatus() { return paymentStatus; }

    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public BigDecimal getFinalPaymentAmount() { return finalPaymentAmount; }

    public void setFinalPaymentAmount(BigDecimal finalPaymentAmount) {
        this.finalPaymentAmount = finalPaymentAmount;
    }

    public boolean isEligible() { return eligible; }

    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public String getUnavailableReason() { return unavailableReason; }

    public void setUnavailableReason(String unavailableReason) {
        this.unavailableReason = unavailableReason;
    }
}
