package underwriting.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class ReinsuranceCreateRequest {

    @NotBlank
    private String reinsurerName;
    @DecimalMin("0")
    private BigDecimal retentionAmount;
    @DecimalMin("0")
    private BigDecimal cessionRate;

    public String getReinsurerName() { return reinsurerName; }
    public void setReinsurerName(String reinsurerName) { this.reinsurerName = reinsurerName; }
    public BigDecimal getRetentionAmount() { return retentionAmount; }
    public void setRetentionAmount(BigDecimal retentionAmount) { this.retentionAmount = retentionAmount; }
    public BigDecimal getCessionRate() { return cessionRate; }
    public void setCessionRate(BigDecimal cessionRate) { this.cessionRate = cessionRate; }
}
