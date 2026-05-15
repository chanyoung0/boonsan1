package model.insurance;

import java.math.BigDecimal;

// 해상보험 — 운송 항로/선박 유형 정보와 위험 평가
public class MarineInsurance extends Insurance {

    private String shippingRoute;
    private String vesselType;

    public MarineInsurance() {}

    // 해상보험 속성으로 초기화
    public MarineInsurance(String productCode, String insurancePeriod, BigDecimal insuredAmount, BigDecimal premium, BigDecimal maturityRefund,
                           String shippingRoute, String vesselType) {
        super(productCode, insurancePeriod, insuredAmount, premium, maturityRefund);
        this.shippingRoute = shippingRoute;
        this.vesselType = vesselType;
    }

    @Override
    public void calculateMaturityRefund() {}

    @Override
    public void calculatePremium() {}

    // 위험도 평가
    public void evaluateRiskLevel() {}

    // 운송 정보 관리
    public void manageShippingInfo() {}

    public String getShippingRoute() { return shippingRoute; }
    public String getVesselType() { return vesselType; }

    public void setShippingRoute(String shippingRoute) { this.shippingRoute = shippingRoute; }
    public void setVesselType(String vesselType) { this.vesselType = vesselType; }

    @Override
    public String toString() {
        return "MarineInsurance{productCode='" + productCode + "', route='" + shippingRoute + "', vessel='" + vesselType + "'}";
    }
}
