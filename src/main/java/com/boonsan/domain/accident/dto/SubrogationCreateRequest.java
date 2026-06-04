package com.boonsan.domain.accident.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SubrogationCreateRequest {

    @NotBlank
    private String targetName;

    @NotBlank
    private String subrogationReason;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal subrogationAmount;

    @NotBlank
    private String employeeNo;

    public String getTargetName() { return targetName; }

    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getSubrogationReason() { return subrogationReason; }

    public void setSubrogationReason(String subrogationReason) { this.subrogationReason = subrogationReason; }

    public BigDecimal getSubrogationAmount() { return subrogationAmount; }

    public void setSubrogationAmount(BigDecimal subrogationAmount) {
        this.subrogationAmount = subrogationAmount;
    }

    public String getEmployeeNo() { return employeeNo; }

    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
}
