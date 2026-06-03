package contract.dto;

import enums.ContractStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class MaturityNoticeResponse {

    private final String policyNumber;
    private final String insuredName;
    private final String insuredContact;
    private final LocalDate contractEndDate;
    private final ContractStatus contractStatus;
    private final long daysUntilMaturity;
    private final String noticeMessage;
    private final String deliveryMethod;
    private final BigDecimal maturityRefundAmount;
    private final LocalDateTime sentAt;
    private final Boolean renewalIntention;
    private final LocalDateTime renewalCheckedAt;

    public MaturityNoticeResponse(
            String policyNumber,
            String insuredName,
            String insuredContact,
            LocalDate contractEndDate,
            ContractStatus contractStatus,
            long daysUntilMaturity,
            String noticeMessage,
            String deliveryMethod,
            BigDecimal maturityRefundAmount,
            LocalDateTime sentAt,
            Boolean renewalIntention,
            LocalDateTime renewalCheckedAt
    ) {
        this.policyNumber = policyNumber;
        this.insuredName = insuredName;
        this.insuredContact = insuredContact;
        this.contractEndDate = contractEndDate;
        this.contractStatus = contractStatus;
        this.daysUntilMaturity = daysUntilMaturity;
        this.noticeMessage = noticeMessage;
        this.deliveryMethod = deliveryMethod;
        this.maturityRefundAmount = maturityRefundAmount;
        this.sentAt = sentAt;
        this.renewalIntention = renewalIntention;
        this.renewalCheckedAt = renewalCheckedAt;
    }

    public String getPolicyNumber() { return policyNumber; }

    public String getInsuredName() { return insuredName; }

    public String getInsuredContact() { return insuredContact; }

    public LocalDate getContractEndDate() { return contractEndDate; }

    public ContractStatus getContractStatus() { return contractStatus; }

    public long getDaysUntilMaturity() { return daysUntilMaturity; }

    public String getNoticeMessage() { return noticeMessage; }

    public String getDeliveryMethod() { return deliveryMethod; }

    public BigDecimal getMaturityRefundAmount() { return maturityRefundAmount; }

    public LocalDateTime getSentAt() { return sentAt; }

    public Boolean getRenewalIntention() { return renewalIntention; }

    public LocalDateTime getRenewalCheckedAt() { return renewalCheckedAt; }
}
