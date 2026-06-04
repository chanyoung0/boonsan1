package com.boonsan.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductDesignRequest {

    @NotBlank
    @Size(max = 255)
    private String productName;

    @NotBlank
    @Size(max = 50)
    private String insuranceTypeCode;

    @Size(max = 255)
    private String targetCustomer;

    @Size(max = 255)
    private String salesChannel;

    @Size(max = 100)
    private String insurancePeriod;

    @Size(max = 100)
    private String paymentPeriod;

    @NotNull
    @Positive
    private BigDecimal insuredAmount;

    private BigDecimal premium;

    private BigDecimal maturityRefund;

    private String mainCoverage;

    private String subscriptionConditions;

    private String rateInformation;

    private String specialContractInfo;

    private Integer driverAge;

    @Size(max = 100)
    private String vehicleType;

    @Size(max = 100)
    private String buildingType;

    @Size(max = 255)
    private String location;

    @Size(max = 255)
    private String shippingRoute;

    @Size(max = 100)
    private String vesselType;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getInsuranceTypeCode() { return insuranceTypeCode; }
    public void setInsuranceTypeCode(String insuranceTypeCode) { this.insuranceTypeCode = insuranceTypeCode; }

    public String getTargetCustomer() { return targetCustomer; }
    public void setTargetCustomer(String targetCustomer) { this.targetCustomer = targetCustomer; }

    public String getSalesChannel() { return salesChannel; }
    public void setSalesChannel(String salesChannel) { this.salesChannel = salesChannel; }

    public String getInsurancePeriod() { return insurancePeriod; }
    public void setInsurancePeriod(String insurancePeriod) { this.insurancePeriod = insurancePeriod; }

    public String getPaymentPeriod() { return paymentPeriod; }
    public void setPaymentPeriod(String paymentPeriod) { this.paymentPeriod = paymentPeriod; }

    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }

    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }

    public BigDecimal getMaturityRefund() { return maturityRefund; }
    public void setMaturityRefund(BigDecimal maturityRefund) { this.maturityRefund = maturityRefund; }

    public String getMainCoverage() { return mainCoverage; }
    public void setMainCoverage(String mainCoverage) { this.mainCoverage = mainCoverage; }

    public String getSubscriptionConditions() { return subscriptionConditions; }
    public void setSubscriptionConditions(String subscriptionConditions) { this.subscriptionConditions = subscriptionConditions; }

    public String getRateInformation() { return rateInformation; }
    public void setRateInformation(String rateInformation) { this.rateInformation = rateInformation; }

    public String getSpecialContractInfo() { return specialContractInfo; }
    public void setSpecialContractInfo(String specialContractInfo) { this.specialContractInfo = specialContractInfo; }

    public Integer getDriverAge() { return driverAge; }
    public void setDriverAge(Integer driverAge) { this.driverAge = driverAge; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getBuildingType() { return buildingType; }
    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getShippingRoute() { return shippingRoute; }
    public void setShippingRoute(String shippingRoute) { this.shippingRoute = shippingRoute; }

    public String getVesselType() { return vesselType; }
    public void setVesselType(String vesselType) { this.vesselType = vesselType; }
}
