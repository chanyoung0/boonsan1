package com.boonsan.contract.dto;

import com.boonsan.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentCollectionCreateRequest {

    @NotNull
    @Min(1)
    private Integer installmentNo;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal plannedAmount;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal collectedAmount;

    @NotNull
    private PaymentMethod paymentMethod;

    public Integer getInstallmentNo() { return installmentNo; }
    public void setInstallmentNo(Integer installmentNo) { this.installmentNo = installmentNo; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getPlannedAmount() { return plannedAmount; }
    public void setPlannedAmount(BigDecimal plannedAmount) { this.plannedAmount = plannedAmount; }

    public BigDecimal getCollectedAmount() { return collectedAmount; }
    public void setCollectedAmount(BigDecimal collectedAmount) { this.collectedAmount = collectedAmount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
