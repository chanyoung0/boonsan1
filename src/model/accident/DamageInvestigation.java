package model.accident;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DamageInvestigation {

    private String adjusterId;
    private float faultRatio;
    private LocalDateTime investigatedAt;
    private BigDecimal lossincome;
    private BigDecimal medicalExpense;
    private BigDecimal settlementAmount;

    public void assessDamageCost() {}

    public void closeCase() {}

    public void requestFraudInvestigation() {}
}
