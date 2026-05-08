package model.contract;

import enums.ReinstatementReason;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reinstatement {

    private LocalDateTime appliedAt;
    private LocalDateTime desiredDate;
    private boolean hasHealthChanged;
    private LocalDate lastPaidDate;
    private LocalDateTime processedAt;
    private ReinstatementReason reinstatementReason;
    private BigDecimal unpaidPremium;

    public void applyReinstatement() {}

    public BigDecimal calculateUnpaidPremium() {
        return null;
    }

    public void processReinstatement() {}
}
