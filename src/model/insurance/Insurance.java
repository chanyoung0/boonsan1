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

    // 만기 환급금 계산 — 납입 보험료 기반으로 maturityRefund 갱신
    public abstract void calculateMaturityRefund();

    // 보험료 계산 — 보험가입금액과 요율로 premium 갱신
    public abstract void calculatePremium();

    // 상품 유효성 검증 — 필수 필드 미설정 시 예외
    public void changeProductStatus() {
        if (productCode == null || productCode.isEmpty())
            throw new IllegalStateException("상품코드가 설정되지 않았습니다.");
        if (insuredAmount == null || insuredAmount.compareTo(java.math.BigDecimal.ZERO) <= 0)
            throw new IllegalStateException("보험가입금액이 유효하지 않습니다.");
    }

    // 보험 종류 반환 — 하위 클래스명 기반
    public String getInsuranceType() {
        return this.getClass().getSimpleName();
    }

    // 상품 정보 필수값 검증
    public void saveProductInfo() {
        if (productCode == null || productCode.isEmpty())
            throw new IllegalArgumentException("상품코드는 필수입니다.");
        if (premium == null || premium.compareTo(java.math.BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("보험료는 0보다 커야 합니다.");
        if (insuredAmount == null || insuredAmount.compareTo(java.math.BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("보험가입금액은 0보다 커야 합니다.");
    }

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
