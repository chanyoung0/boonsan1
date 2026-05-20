package model.insurance;

import model.accident.AccidentHistory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// 자동차보험 도메인 모델 — 차량 정보 기반 자동차보험 상품 관리
public class AutoInsurance extends Insurance {

    private int driverAge;
    private String vehicleType;

    public AutoInsurance() { super(); }

    public AutoInsurance(String productCode, String insurancePeriod, BigDecimal insuredAmount,
                         BigDecimal premium, BigDecimal maturityRefund,
                         int driverAge, String vehicleType) {
        super(productCode, insurancePeriod, insuredAmount, premium, maturityRefund);
        this.driverAge = driverAge;
        this.vehicleType = vehicleType;
    }

    @Override
    public void calculatePremium()        {}

    @Override
    public void calculateMaturityRefund() {}

    // 사고 이력 목록 조회
    public List<AccidentHistory> getAccidentHistory() { return new ArrayList<>(); }

    public int    getDriverAge()             { return driverAge; }
    public void   setDriverAge(int v)        { this.driverAge = v; }
    public String getVehicleType()           { return vehicleType; }
    public void   setVehicleType(String v)   { this.vehicleType = v; }

    @Override
    public String toString() {
        return "AutoInsurance{productCode='" + productCode + "', vehicleType='" + vehicleType + "', driverAge=" + driverAge + "}";
    }
}
