package com.boonsan.contract.dto;

import com.boonsan.enums.ContractStatus;

import java.time.LocalDate;

public class MaturityNoticeResponse {

    private final String policyNumber;
    private final String insuredName;
    private final String insuredContact;
    private final LocalDate contractEndDate;
    private final ContractStatus contractStatus;
    private final long daysUntilMaturity;
    private final String noticeMessage;
    private final String deliveryMethod;

    public MaturityNoticeResponse(
            String policyNumber,
            String insuredName,
            String insuredContact,
            LocalDate contractEndDate,
            ContractStatus contractStatus,
            long daysUntilMaturity,
            String noticeMessage,
            String deliveryMethod
    ) {
        this.policyNumber = policyNumber;
        this.insuredName = insuredName;
        this.insuredContact = insuredContact;
        this.contractEndDate = contractEndDate;
        this.contractStatus = contractStatus;
        this.daysUntilMaturity = daysUntilMaturity;
        this.noticeMessage = noticeMessage;
        this.deliveryMethod = deliveryMethod;
    }

    public String getPolicyNumber() { return policyNumber; }

    public String getInsuredName() { return insuredName; }

    public String getInsuredContact() { return insuredContact; }

    public LocalDate getContractEndDate() { return contractEndDate; }

    public ContractStatus getContractStatus() { return contractStatus; }

    public long getDaysUntilMaturity() { return daysUntilMaturity; }

    public String getNoticeMessage() { return noticeMessage; }

    public String getDeliveryMethod() { return deliveryMethod; }
}
