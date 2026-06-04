package contract.dto;

import enums.ReinstatementReason;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReinstatementCreateRequest {

    @NotNull
    private ReinstatementReason reinstatementReason;

    @NotNull
    private LocalDate desiredDate;

    @NotNull
    private Boolean hasHealthChanged;

    private LocalDate lastPaidDate;

    @NotNull
    @Min(1)
    private Integer unpaidInstallmentCount;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal premiumPerInstallment;

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
}
