package model.insurance;

import model.accident.AccidentHistory;

import java.math.BigDecimal;
import java.util.List;

public class AutoInsurance extends Insurance {

    private int drivingAge;
    private String location;
    private String vehicleType;

    @Override
    public BigDecimal calculatePremium() {
        return null;
    }

    @Override
    public BigDecimal calculateMaturityRefund() {
        return null;
    }

    public List<AccidentHistory> getAccidentHistory() {
        return null;
    }
}
