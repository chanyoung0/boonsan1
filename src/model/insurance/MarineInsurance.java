package model.insurance;

import enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MarineInsurance extends Insurance {

    private String shippingRoute;
    private String vesselType;

    public MarineInsurance() {}

    public MarineInsurance(
            String productCode,
            String productName,
            String insuranceTypeCode,
            String targetCustomer,
            String salesChannel,
            String insurancePeriod,
            String paymentPeriod,
            BigDecimal insuredAmount,
            BigDecimal premium,
            BigDecimal maturityRefund,
            String mainCoverage,
            String subscriptionConditions,
            String rateInformation,
            String specialContractInfo,
            ProductStatus productStatus,
            LocalDateTime createdAt,
            String shippingRoute,
            String vesselType
    ) {
        super(productCode, productName, insuranceTypeCode, targetCustomer, salesChannel,
                insurancePeriod, paymentPeriod, insuredAmount, premium, maturityRefund,
                mainCoverage, subscriptionConditions, rateInformation, specialContractInfo,
                productStatus, createdAt);
        this.shippingRoute = shippingRoute;
        this.vesselType = vesselType;
    }

    @Override
    public void calculateMaturityRefund() {}

    @Override
    public void calculatePremium() {}

    public void evaluateRiskLevel() {}

    public void manageShippingInfo() {}

    public String getShippingRoute() { return shippingRoute; }
    public void setShippingRoute(String shippingRoute) { this.shippingRoute = shippingRoute; }

    public String getVesselType() { return vesselType; }
    public void setVesselType(String vesselType) { this.vesselType = vesselType; }
}
