package model.insurance;

import enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FireInsurance extends Insurance {

    private String buildingType;
    private String location;

    public FireInsurance() {}

    public FireInsurance(
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
            String buildingType,
            String location
    ) {
        super(productCode, productName, insuranceTypeCode, targetCustomer, salesChannel,
                insurancePeriod, paymentPeriod, insuredAmount, premium, maturityRefund,
                mainCoverage, subscriptionConditions, rateInformation, specialContractInfo,
                productStatus, createdAt);
        this.buildingType = buildingType;
        this.location = location;
    }

    @Override
    public void calculateMaturityRefund() {}

    @Override
    public void calculatePremium() {}

    public void analyzeRiskFactors() {}

    public void setCoverageScope() {}

    public String getBuildingType() { return buildingType; }
    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
