package model.insurance;

import java.math.BigDecimal;

// 해상보험 도메인 모델 — 선박 유형 및 항로 기반 해상보험 상품 관리
public class MarineInsurance extends Insurance {

    private String shippingRoute;
    private String vesselType;

    public MarineInsurance() { super(); }

    public MarineInsurance(String productCode, String insurancePeriod, BigDecimal insuredAmount,
                           BigDecimal premium, BigDecimal maturityRefund,
                           String vesselType, String shippingRoute) {
        super(productCode, insurancePeriod, insuredAmount, premium, maturityRefund);
        this.vesselType = vesselType;
        this.shippingRoute = shippingRoute;
    }

    @Override
    public void calculateMaturityRefund() {
        if (premium == null) return;
        this.maturityRefund = premium.multiply(new BigDecimal("0.1"));
    }

    @Override
    public void calculatePremium() {
        if (insuredAmount == null) return;
        // 화물선 할증 요율, 그 외 기본 요율
        double rate = "화물선".equals(vesselType) ? 0.0040 : 0.0030;
        this.premium = insuredAmount.multiply(BigDecimal.valueOf(rate));
    }

    public void evaluateRiskLevel() {
        if (vesselType == null || vesselType.isEmpty())
            throw new IllegalStateException("선박 유형이 설정되지 않았습니다.");
        if (shippingRoute == null || shippingRoute.isEmpty())
            throw new IllegalStateException("항로가 설정되지 않았습니다.");
    }

    public void manageShippingInfo() {
        if (shippingRoute == null || shippingRoute.isEmpty())
            throw new IllegalStateException("항로 정보가 없습니다.");
    }

    public String getShippingRoute()            { return shippingRoute; }
    public void   setShippingRoute(String v)    { this.shippingRoute = v; }
    public String getVesselType()               { return vesselType; }
    public void   setVesselType(String v)       { this.vesselType = v; }

    @Override
    public String toString() {
        return "MarineInsurance{productCode='" + productCode + "', vesselType='" + vesselType + "', shippingRoute='" + shippingRoute + "'}";
    }
}
