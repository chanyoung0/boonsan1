package model.insurance;

import enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 보험 상품 추상 클래스 — 자동차/화재/해상 보험의 공통 속성 및 기능 정의
public abstract class Insurance {

    protected String productCode;
    protected String productName;
    protected String insuranceTypeCode;
    protected String targetCustomer;
    protected String salesChannel;
    protected String insurancePeriod;
    protected String paymentPeriod;
    protected BigDecimal insuredAmount;
    protected BigDecimal premium;
    protected BigDecimal maturityRefund;
    protected String mainCoverage;
    protected String subscriptionConditions;
    protected String rateInformation;
    protected String specialContractInfo;
    protected ProductStatus productStatus;
    protected LocalDateTime createdAt;

    protected Insurance() {}

    protected Insurance(
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
        this.createdAt = createdAt;
    }

    // 만기 환급금 계산
    public abstract void calculateMaturityRefund();

    // 보험료 계산
    public abstract void calculatePremium();

    // 상품 상태 변경
    public void changeProductStatus() {}

    // 보험 종류 조회
    public void getInsuranceType() {}

    // 상품 정보 저장
    public void saveProductInfo() {}

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

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

    public ProductStatus getProductStatus() { return productStatus; }
    public void setProductStatus(ProductStatus productStatus) { this.productStatus = productStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
