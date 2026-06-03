package contract.dto;

import enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaturityTargetResponse {

    private String policyNumber;
    private String insuredName;
    private String insuredContact;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private ContractStatus contractStatus;
    private BigDecimal insuredAmount;
    private BigDecimal maturityRefundAmount;
    private Long daysUntilMaturity;
    private String maturityTiming;
    private LocalDateTime noticeSentAt;
    private Boolean renewalIntention;
    private LocalDateTime renewalCheckedAt;

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getInsuredName() { return insuredName; }
    public void setInsuredName(String insuredName) { this.insuredName = insuredName; }

    public String getInsuredContact() { return insuredContact; }
    public void setInsuredContact(String insuredContact) { this.insuredContact = insuredContact; }

    public LocalDate getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(LocalDate contractStartDate) { this.contractStartDate = contractStartDate; }

    public LocalDate getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(LocalDate contractEndDate) { this.contractEndDate = contractEndDate; }

    public ContractStatus getContractStatus() { return contractStatus; }
    public void setContractStatus(ContractStatus contractStatus) { this.contractStatus = contractStatus; }

    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }

    public BigDecimal getMaturityRefundAmount() { return maturityRefundAmount; }
    public void setMaturityRefundAmount(BigDecimal maturityRefundAmount) {
        this.maturityRefundAmount = maturityRefundAmount;
    }

    public Long getDaysUntilMaturity() { return daysUntilMaturity; }
    public void setDaysUntilMaturity(Long daysUntilMaturity) { this.daysUntilMaturity = daysUntilMaturity; }

    public String getMaturityTiming() { return maturityTiming; }
    public void setMaturityTiming(String maturityTiming) { this.maturityTiming = maturityTiming; }

    public LocalDateTime getNoticeSentAt() { return noticeSentAt; }
    public void setNoticeSentAt(LocalDateTime noticeSentAt) { this.noticeSentAt = noticeSentAt; }

    public Boolean getRenewalIntention() { return renewalIntention; }
    public void setRenewalIntention(Boolean renewalIntention) { this.renewalIntention = renewalIntention; }

    public LocalDateTime getRenewalCheckedAt() { return renewalCheckedAt; }
    public void setRenewalCheckedAt(LocalDateTime renewalCheckedAt) {
        this.renewalCheckedAt = renewalCheckedAt;
    }
}
