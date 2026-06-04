package contract.dto;

import enums.ReinstatementReason;
import enums.ReinstatementStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReinstatementResponse {

    private String reinstatementId;
    private String policyNumber;
    private ReinstatementReason reinstatementReason;
    private LocalDate desiredDate;
    private Boolean hasHealthChanged;
    private LocalDate lastPaidDate;
    private Integer unpaidInstallmentCount;
    private BigDecimal premiumPerInstallment;
    private BigDecimal unpaidPremium;
    private ReinstatementStatus reinstatementStatus;
    private String underwritingRequestId;
    private LocalDateTime appliedAt;
    private LocalDateTime unpaidSettledAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    public String getReinstatementId() { return reinstatementId; }
    public void setReinstatementId(String reinstatementId) { this.reinstatementId = reinstatementId; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public ReinstatementReason getReinstatementReason() { return reinstatementReason; }
    public void setReinstatementReason(ReinstatementReason reinstatementReason) {
        this.reinstatementReason = reinstatementReason;
    }

    public LocalDate getDesiredDate() { return desiredDate; }
    public void setDesiredDate(LocalDate desiredDate) { this.desiredDate = desiredDate; }

    public Boolean getHasHealthChanged() { return hasHealthChanged; }
    public void setHasHealthChanged(Boolean hasHealthChanged) { this.hasHealthChanged = hasHealthChanged; }

    public LocalDate getLastPaidDate() { return lastPaidDate; }
    public void setLastPaidDate(LocalDate lastPaidDate) { this.lastPaidDate = lastPaidDate; }

    public Integer getUnpaidInstallmentCount() { return unpaidInstallmentCount; }
    public void setUnpaidInstallmentCount(Integer unpaidInstallmentCount) {
        this.unpaidInstallmentCount = unpaidInstallmentCount;
    }

    public BigDecimal getPremiumPerInstallment() { return premiumPerInstallment; }
    public void setPremiumPerInstallment(BigDecimal premiumPerInstallment) {
        this.premiumPerInstallment = premiumPerInstallment;
    }

    public BigDecimal getUnpaidPremium() { return unpaidPremium; }
    public void setUnpaidPremium(BigDecimal unpaidPremium) { this.unpaidPremium = unpaidPremium; }

    public ReinstatementStatus getReinstatementStatus() { return reinstatementStatus; }
    public void setReinstatementStatus(ReinstatementStatus reinstatementStatus) {
        this.reinstatementStatus = reinstatementStatus;
    }

    public String getUnderwritingRequestId() { return underwritingRequestId; }
    public void setUnderwritingRequestId(String underwritingRequestId) {
        this.underwritingRequestId = underwritingRequestId;
    }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getUnpaidSettledAt() { return unpaidSettledAt; }
    public void setUnpaidSettledAt(LocalDateTime unpaidSettledAt) { this.unpaidSettledAt = unpaidSettledAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
