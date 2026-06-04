package com.boonsan.domain.model.contract;

import com.boonsan.domain.enums.ReinstatementReason;

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

    public void calculateUnpaidPremium() {}

    public void processReinstatement() {}
}
