package model.insurance;

import model.accident.AccidentHistory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// 자동차보험 — 운전자/차종 정보와 사고 이력 보유
public class AutoInsurance extends Insurance {

    private int driverAge;
    private String vehicleType;
    private final List<AccidentHistory> accidentHistories = new ArrayList<>();

    public AutoInsurance() {}

    // 자동차보험 속성으로 초기화
    public AutoInsurance(String productCode, String insurancePeriod, BigDecimal insuredAmount, BigDecimal premium, BigDecimal maturityRefund,
                         int driverAge, String vehicleType) {
        super(productCode, insurancePeriod, insuredAmount, premium, maturityRefund);
        this.driverAge = driverAge;
        this.vehicleType = vehicleType;
    }

    @Override
    public void calculatePremium() {}

    @Override
    public void calculateMaturityRefund() {}

    // 사고 이력 목록 조회
    public List<AccidentHistory> getAccidentHistory() {
        return accidentHistories;
    }

    public void addAccidentHistory(AccidentHistory h) { this.accidentHistories.add(h); }

    public int getDriverAge() { return driverAge; }
    public String getVehicleType() { return vehicleType; }

    public void setDriverAge(int driverAge) { this.driverAge = driverAge; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    @Override
    public String toString() {
        return "AutoInsurance{productCode='" + productCode + "', driverAge=" + driverAge + ", vehicleType='" + vehicleType + "'}";
    }
}
