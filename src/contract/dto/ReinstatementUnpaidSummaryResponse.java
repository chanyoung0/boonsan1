package contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReinstatementUnpaidSummaryResponse {

    private String policyNumber;
    private Integer unpaidInstallmentCount;
    private BigDecimal premiumPerInstallment;
    private BigDecimal unpaidPremium;
    private LocalDate lastPaidDate;

    public ReinstatementUnpaidSummaryResponse() {
    }

    public ReinstatementUnpaidSummaryResponse(
            String policyNumber,
            Integer unpaidInstallmentCount,
            BigDecimal premiumPerInstallment,
            BigDecimal unpaidPremium,
            LocalDate lastPaidDate
    ) {
        this.policyNumber = policyNumber;
        this.unpaidInstallmentCount = unpaidInstallmentCount;
        this.premiumPerInstallment = premiumPerInstallment;
        this.unpaidPremium = unpaidPremium;
        this.lastPaidDate = lastPaidDate;
    }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

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

    public LocalDate getLastPaidDate() { return lastPaidDate; }
    public void setLastPaidDate(LocalDate lastPaidDate) { this.lastPaidDate = lastPaidDate; }
}
