package com.boonsan.contract.dto;

import com.boonsan.enums.PaymentMethod;
import com.boonsan.enums.ProcessingResult;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UnpaidNoticeResponse {

    private final String collectionId;
    private final String policyNumber;
    private final String insuredName;
    private final String insuredContact;
    private final Integer installmentNo;
    private final LocalDate dueDate;
    private final long daysOverdue;
    private final BigDecimal unpaidAmount;
    private final BigDecimal lateFee;
    private final BigDecimal totalAmountDue;
    private final PaymentMethod paymentMethod;
    private final ProcessingResult processingResult;
    private final String noticeMessage;
    private final String deliveryMethod;

    public UnpaidNoticeResponse(
            String collectionId,
            String policyNumber,
            String insuredName,
            String insuredContact,
            Integer installmentNo,
            LocalDate dueDate,
            long daysOverdue,
            BigDecimal unpaidAmount,
            BigDecimal lateFee,
            BigDecimal totalAmountDue,
            PaymentMethod paymentMethod,
            ProcessingResult processingResult,
            String noticeMessage,
            String deliveryMethod
    ) {
        this.collectionId = collectionId;
        this.policyNumber = policyNumber;
        this.insuredName = insuredName;
        this.insuredContact = insuredContact;
        this.installmentNo = installmentNo;
        this.dueDate = dueDate;
        this.daysOverdue = daysOverdue;
        this.unpaidAmount = unpaidAmount;
        this.lateFee = lateFee;
        this.totalAmountDue = totalAmountDue;
        this.paymentMethod = paymentMethod;
        this.processingResult = processingResult;
        this.noticeMessage = noticeMessage;
        this.deliveryMethod = deliveryMethod;
    }

    public String getCollectionId() { return collectionId; }
    public String getPolicyNumber() { return policyNumber; }
    public String getInsuredName() { return insuredName; }
    public String getInsuredContact() { return insuredContact; }
    public Integer getInstallmentNo() { return installmentNo; }
    public LocalDate getDueDate() { return dueDate; }
    public long getDaysOverdue() { return daysOverdue; }
    public BigDecimal getUnpaidAmount() { return unpaidAmount; }
    public BigDecimal getLateFee() { return lateFee; }
    public BigDecimal getTotalAmountDue() { return totalAmountDue; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public ProcessingResult getProcessingResult() { return processingResult; }
    public String getNoticeMessage() { return noticeMessage; }
    public String getDeliveryMethod() { return deliveryMethod; }
}
