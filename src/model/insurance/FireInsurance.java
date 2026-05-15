package model.insurance;

import java.math.BigDecimal;

// 화재보험 도메인 모델 — 건물 유형 및 위치 기반 화재보험 상품 관리
public class FireInsurance extends Insurance {

    private String buildingType;
    private String location;

    public FireInsurance() { super(); }

    public FireInsurance(String productCode, String insurancePeriod, BigDecimal insuredAmount,
                         BigDecimal premium, BigDecimal maturityRefund,
                         String buildingType, String location) {
        super(productCode, insurancePeriod, insuredAmount, premium, maturityRefund);
        this.buildingType = buildingType;
        this.location = location;
    }

    @Override
    public void calculateMaturityRefund() {}

    @Override
    public void calculatePremium()        {}

    public void analyzeRiskFactors()      {}
    public void setCoverageScope()        {}

    public String getBuildingType()           { return buildingType; }
    public void   setBuildingType(String v)   { this.buildingType = v; }
    public String getLocation()               { return location; }
    public void   setLocation(String v)       { this.location = v; }

    @Override
    public String toString() {
        return "FireInsurance{productCode='" + productCode + "', buildingType='" + buildingType + "', location='" + location + "'}";
    }
}
