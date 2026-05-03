package insurance;

import java.math.BigDecimal;

public class FireInsurance extends Insurance {

    private String buildingType;
    private String location;

    @Override
    public BigDecimal calculateMaturityRefund() {
        return null;
    }

    @Override
    public BigDecimal calculatePremium() {
        return null;
    }

    public void analyzeRiskFactor() {}

    public void setCoverageScope() {}
}
