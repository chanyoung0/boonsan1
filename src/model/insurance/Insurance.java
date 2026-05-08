package model.insurance;

import java.math.BigDecimal;

public abstract class Insurance {

    protected String insurancePeriod;
    protected BigDecimal premium;
    protected BigDecimal maturityRefund;
    protected String productCode;

    public abstract BigDecimal calculateMaturityRefund();

    public abstract BigDecimal calculatePremium();

    public void changeProductStatus() {}

    public void saveProductInfo() {}
}
