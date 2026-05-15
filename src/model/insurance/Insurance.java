package model.insurance;

import java.math.BigDecimal;

// 보험 상품 추상 클래스 — 자동차/화재/해상 보험의 공통 속성 및 기능 정의
public abstract class Insurance {

    protected String productCode;
    protected String insurancePeriod;
    protected BigDecimal insuredAmount;
    protected BigDecimal premium;
    protected BigDecimal maturityRefund;

    public Insurance() {}

    // 공통 보험 속성으로 초기화
    public Insurance(String productCode, String insurancePeriod, BigDecimal insuredAmount, BigDecimal premium, BigDecimal maturityRefund) {
        this.productCode = productCode;
        this.insurancePeriod = insurancePeriod;
        this.insuredAmount = insuredAmount;
        this.premium = premium;
        this.maturityRefund = maturityRefund;
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
    public String getInsurancePeriod() { return insurancePeriod; }
    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public BigDecimal getPremium() { return premium; }
    public BigDecimal getMaturityRefund() { return maturityRefund; }

    public void setProductCode(String productCode) { this.productCode = productCode; }
    public void setInsurancePeriod(String insurancePeriod) { this.insurancePeriod = insurancePeriod; }
    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }
    public void setMaturityRefund(BigDecimal maturityRefund) { this.maturityRefund = maturityRefund; }
}
