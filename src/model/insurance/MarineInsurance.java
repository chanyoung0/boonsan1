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
    public void calculateMaturityRefund() {}

    @Override
    public void calculatePremium()        {}

    public void evaluateRiskLevel()       {}
    public void manageShippingInfo()      {}

    public String getShippingRoute()            { return shippingRoute; }
    public void   setShippingRoute(String v)    { this.shippingRoute = v; }
    public String getVesselType()               { return vesselType; }
    public void   setVesselType(String v)       { this.vesselType = v; }

    @Override
    public String toString() {
        return "MarineInsurance{productCode='" + productCode + "', vesselType='" + vesselType + "', shippingRoute='" + shippingRoute + "'}";
    }
}
