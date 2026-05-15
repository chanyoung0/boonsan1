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
    public void calculateMaturityRefund() {
        if (premium == null) return;
        this.maturityRefund = premium.multiply(new BigDecimal("0.1"));
    }

    @Override
    public void calculatePremium() {
        if (insuredAmount == null) return;
        // 아파트는 할인 요율, 그 외 기본 요율
        double rate = "아파트".equals(buildingType) ? 0.0015 : 0.0020;
        this.premium = insuredAmount.multiply(BigDecimal.valueOf(rate));
    }

    public void analyzeRiskFactors() {
        if (buildingType == null || buildingType.isEmpty())
            throw new IllegalStateException("건물 유형이 설정되지 않았습니다.");
        if (location == null || location.isEmpty())
            throw new IllegalStateException("건물 소재지가 설정되지 않았습니다.");
    }

    public void setCoverageScope() {
        if (insuredAmount == null || insuredAmount.compareTo(java.math.BigDecimal.ZERO) <= 0)
            throw new IllegalStateException("보험가입금액이 유효하지 않습니다.");
    }

    public String getBuildingType()           { return buildingType; }
    public void   setBuildingType(String v)   { this.buildingType = v; }
    public String getLocation()               { return location; }
    public void   setLocation(String v)       { this.location = v; }

    @Override
    public String toString() {
        return "FireInsurance{productCode='" + productCode + "', buildingType='" + buildingType + "', location='" + location + "'}";
    }
}
