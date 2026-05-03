package underwriting;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Reinsurance {

    private LocalDateTime accountingDate;
    private float cessionRate;
    private String contract;
    private LocalDateTime expectedSettlementDate;
    private String reinsuranceMethod;
    private String reinsuranceName;
    private float retentionRate;
    private BigDecimal settlementAmount;
    private String settlementMethod;

    public BigDecimal calculatePremium() {
        return null;
    }

    public void recognizeAccounting() {}

    public void setConditions() {}

    public void registerSettlementSchedule() {}
}
