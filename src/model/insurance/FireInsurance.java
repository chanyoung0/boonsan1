package model.insurance;

import java.math.BigDecimal;

// 화재보험 — 건물 유형/소재지 정보와 위험요인 분석
public class FireInsurance extends Insurance {

    private String buildingType;
    private String location;

    public FireInsurance() {}

    // 화재보험 속성으로 초기화
    public FireInsurance(String productCode, String insurancePeriod, BigDecimal insuredAmount, BigDecimal premium, BigDecimal maturityRefund,
                         String buildingType, String location) {
        super(productCode, insurancePeriod, insuredAmount, premium, maturityRefund);
        this.buildingType = buildingType;
        this.location = location;
    }

    @Override
    public void calculateMaturityRefund() {}

    @Override
    public void calculatePremium() {}

    // 위험 요인 분석
    public void analyzeRiskFactors() {}

    // 보장 범위 설정
    public void setCoverageScope() {}

    public String getBuildingType() { return buildingType; }
    public String getLocation() { return location; }

    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return "FireInsurance{productCode='" + productCode + "', building='" + buildingType + "', location='" + location + "'}";
    }
}
