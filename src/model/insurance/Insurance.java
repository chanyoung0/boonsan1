package model.insurance;

import java.math.BigDecimal;

// 보험 상품 추상 클래스 — 자동차/화재/해상 보험의 공통 속성 및 기능 정의
public abstract class Insurance {

    protected String insurancePeriod;
    protected BigDecimal premium;
    protected BigDecimal maturityRefund;
    protected String productCode;

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
}
