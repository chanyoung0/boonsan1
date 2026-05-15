package model.insurance;

import java.math.BigDecimal;

public class MarineInsurance extends Insurance {

    private String shippingRoute;
    private String vesselType;

    @Override
    public BigDecimal calculateMaturityRefund() {
        return null;
    }

    @Override
    public BigDecimal calculatePremium() {
        return null;
    }

    public void evaluateRiskLevel() {}

    public void manageShippingInfo() {}
}
