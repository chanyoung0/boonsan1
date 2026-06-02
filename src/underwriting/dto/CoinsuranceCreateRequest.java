package underwriting.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class CoinsuranceCreateRequest {

    @NotBlank
    private String coinsurerName;
    private boolean manualSelected;
    @DecimalMin("0")
    private BigDecimal retainedAmount;
    @DecimalMin("0")
    private BigDecimal shareRate;

    public String getCoinsurerName() { return coinsurerName; }
    public void setCoinsurerName(String coinsurerName) { this.coinsurerName = coinsurerName; }
    public boolean isManualSelected() { return manualSelected; }
    public void setManualSelected(boolean manualSelected) { this.manualSelected = manualSelected; }
    public BigDecimal getRetainedAmount() { return retainedAmount; }
    public void setRetainedAmount(BigDecimal retainedAmount) { this.retainedAmount = retainedAmount; }
    public BigDecimal getShareRate() { return shareRate; }
    public void setShareRate(BigDecimal shareRate) { this.shareRate = shareRate; }
}
