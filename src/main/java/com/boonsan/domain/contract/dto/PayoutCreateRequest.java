package com.boonsan.domain.contract.dto;

import com.boonsan.domain.enums.CalculationBasis;
import com.boonsan.domain.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PayoutCreateRequest {

    @NotNull
    private CalculationBasis calculationBasis;

    @NotNull
    private PaymentType paymentType;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal paidPremiumAmount;

    @Size(max = 255)
    private String deductionItem;

    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal deductionAmount;

    public CalculationBasis getCalculationBasis() { return calculationBasis; }

    public void setCalculationBasis(CalculationBasis calculationBasis) { this.calculationBasis = calculationBasis; }

    public PaymentType getPaymentType() { return paymentType; }

    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    public BigDecimal getPaidPremiumAmount() { return paidPremiumAmount; }

    public void setPaidPremiumAmount(BigDecimal paidPremiumAmount) { this.paidPremiumAmount = paidPremiumAmount; }

    public String getDeductionItem() { return deductionItem; }

    public void setDeductionItem(String deductionItem) { this.deductionItem = deductionItem; }

    public BigDecimal getDeductionAmount() { return deductionAmount; }

    public void setDeductionAmount(BigDecimal deductionAmount) { this.deductionAmount = deductionAmount; }
}
