package com.boonsan.model.insurance;

import com.boonsan.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AutoInsurance extends Insurance {

    private int driverAge;
    private String vehicleType;

    public AutoInsurance() {}

    public AutoInsurance(
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
            int driverAge,
            String vehicleType
    ) {
        super(productCode, productName, insuranceTypeCode, targetCustomer, salesChannel,
                insurancePeriod, paymentPeriod, insuredAmount, premium, maturityRefund,
                mainCoverage, subscriptionConditions, rateInformation, specialContractInfo,
                productStatus, createdAt);
        this.driverAge = driverAge;
        this.vehicleType = vehicleType;
    }

    @Override
    public void calculatePremium() {}

    @Override
    public void calculateMaturityRefund() {}

    public void getAccidentHistory() {}

    public int getDriverAge() { return driverAge; }
    public void setDriverAge(int driverAge) { this.driverAge = driverAge; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}
