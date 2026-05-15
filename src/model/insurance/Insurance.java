package model.insurance;

import java.math.BigDecimal;

// 보험 상품 추상 클래스 — 자동차/화재/해상 보험의 공통 속성 및 기능 정의
public abstract class Insurance {

    protected String insurancePeriod;
    protected BigDecimal insuredAmount;
    protected BigDecimal maturityRefund;
    protected BigDecimal premium;
    protected String productCode;

    protected Insurance() {}

    protected Insurance(String productCode, String insurancePeriod, BigDecimal insuredAmount,
                        BigDecimal premium, BigDecimal maturityRefund) {
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

    public String     getProductCode()               { return productCode; }
    public void       setProductCode(String v)       { this.productCode = v; }
    public String     getInsurancePeriod()           { return insurancePeriod; }
    public void       setInsurancePeriod(String v)   { this.insurancePeriod = v; }
    public BigDecimal getInsuredAmount()             { return insuredAmount; }
    public void       setInsuredAmount(BigDecimal v) { this.insuredAmount = v; }
    public BigDecimal getPremium()                   { return premium; }
    public void       setPremium(BigDecimal v)       { this.premium = v; }
    public BigDecimal getMaturityRefund()            { return maturityRefund; }
    public void       setMaturityRefund(BigDecimal v){ this.maturityRefund = v; }

    @Override
    public String toString() {
        return "Insurance{productCode='" + productCode + "', insurancePeriod='" + insurancePeriod + "'}";
    }
}
