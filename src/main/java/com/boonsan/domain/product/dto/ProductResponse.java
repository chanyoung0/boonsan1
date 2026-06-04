package com.boonsan.domain.product.dto;

import com.boonsan.domain.enums.ProductStatus;
import com.boonsan.domain.model.insurance.AutoInsurance;
import com.boonsan.domain.model.insurance.FireInsurance;
import com.boonsan.domain.model.insurance.Insurance;
import com.boonsan.domain.model.insurance.MarineInsurance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {

    private final String productCode;
    private final String productName;
    private final String insuranceTypeCode;
    private final String targetCustomer;
    private final String salesChannel;
    private final String insurancePeriod;
    private final String paymentPeriod;
    private final BigDecimal insuredAmount;
    private final BigDecimal premium;
    private final BigDecimal maturityRefund;
    private final String mainCoverage;
    private final String subscriptionConditions;
    private final String rateInformation;
    private final String specialContractInfo;
    private final ProductStatus productStatus;
    private final Integer driverAge;
    private final String vehicleType;
    private final String buildingType;
    private final String location;
    private final String shippingRoute;
    private final String vesselType;
    private final LocalDateTime createdAt;

    private ProductResponse(
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
            Integer driverAge,
            String vehicleType,
            String buildingType,
            String location,
            String shippingRoute,
            String vesselType,
            LocalDateTime createdAt
    ) {
        this.productCode = productCode;
        this.productName = productName;
        this.insuranceTypeCode = insuranceTypeCode;
        this.targetCustomer = targetCustomer;
        this.salesChannel = salesChannel;
        this.insurancePeriod = insurancePeriod;
        this.paymentPeriod = paymentPeriod;
        this.insuredAmount = insuredAmount;
        this.premium = premium;
        this.maturityRefund = maturityRefund;
        this.mainCoverage = mainCoverage;
        this.subscriptionConditions = subscriptionConditions;
        this.rateInformation = rateInformation;
        this.specialContractInfo = specialContractInfo;
        this.productStatus = productStatus;
        this.driverAge = driverAge;
        this.vehicleType = vehicleType;
        this.buildingType = buildingType;
        this.location = location;
        this.shippingRoute = shippingRoute;
        this.vesselType = vesselType;
        this.createdAt = createdAt;
    }

    public static ProductResponse from(Insurance insurance) {
        Integer driverAge = null;
        String vehicleType = null;
        String buildingType = null;
        String location = null;
        String shippingRoute = null;
        String vesselType = null;

        if (insurance instanceof AutoInsurance auto) {
            driverAge = auto.getDriverAge();
            vehicleType = auto.getVehicleType();
        } else if (insurance instanceof FireInsurance fire) {
            buildingType = fire.getBuildingType();
            location = fire.getLocation();
        } else if (insurance instanceof MarineInsurance marine) {
            shippingRoute = marine.getShippingRoute();
            vesselType = marine.getVesselType();
        }

        return new ProductResponse(
                insurance.getProductCode(),
                insurance.getProductName(),
                insurance.getInsuranceTypeCode(),
                insurance.getTargetCustomer(),
                insurance.getSalesChannel(),
                insurance.getInsurancePeriod(),
                insurance.getPaymentPeriod(),
                insurance.getInsuredAmount(),
                insurance.getPremium(),
                insurance.getMaturityRefund(),
                insurance.getMainCoverage(),
                insurance.getSubscriptionConditions(),
                insurance.getRateInformation(),
                insurance.getSpecialContractInfo(),
                insurance.getProductStatus(),
                driverAge,
                vehicleType,
                buildingType,
                location,
                shippingRoute,
                vesselType,
                insurance.getCreatedAt()
        );
    }

    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public String getInsuranceTypeCode() { return insuranceTypeCode; }
    public String getTargetCustomer() { return targetCustomer; }
    public String getSalesChannel() { return salesChannel; }
    public String getInsurancePeriod() { return insurancePeriod; }
    public String getPaymentPeriod() { return paymentPeriod; }
    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public BigDecimal getPremium() { return premium; }
    public BigDecimal getMaturityRefund() { return maturityRefund; }
    public String getMainCoverage() { return mainCoverage; }
    public String getSubscriptionConditions() { return subscriptionConditions; }
    public String getRateInformation() { return rateInformation; }
    public String getSpecialContractInfo() { return specialContractInfo; }
    public ProductStatus getProductStatus() { return productStatus; }
    public Integer getDriverAge() { return driverAge; }
    public String getVehicleType() { return vehicleType; }
    public String getBuildingType() { return buildingType; }
    public String getLocation() { return location; }
    public String getShippingRoute() { return shippingRoute; }
    public String getVesselType() { return vesselType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
