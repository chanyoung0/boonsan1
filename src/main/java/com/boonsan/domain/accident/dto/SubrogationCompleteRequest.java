package com.boonsan.domain.accident.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SubrogationCompleteRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal recoveredAmount;

    public BigDecimal getRecoveredAmount() { return recoveredAmount; }

    public void setRecoveredAmount(BigDecimal recoveredAmount) {
        this.recoveredAmount = recoveredAmount;
    }
}
