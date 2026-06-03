package product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// 보험요율 산정 요청 DTO — 가입금액과 5개 요율 입력값 (% 단위)
public class PremiumEstimateRequest {

    @NotNull
    @Positive
    private BigDecimal insuredAmount;

    @NotNull
    private BigDecimal baseRate;

    @NotNull
    private BigDecimal riskRate;

    @NotNull
    private BigDecimal expectedInterestRate;

    @NotNull
    private BigDecimal operatingExpenseRatio;

    @NotNull
    private BigDecimal discountSurchargeRate;

    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }

    public BigDecimal getBaseRate() { return baseRate; }
    public void setBaseRate(BigDecimal baseRate) { this.baseRate = baseRate; }

    public BigDecimal getRiskRate() { return riskRate; }
    public void setRiskRate(BigDecimal riskRate) { this.riskRate = riskRate; }

    public BigDecimal getExpectedInterestRate() { return expectedInterestRate; }
    public void setExpectedInterestRate(BigDecimal expectedInterestRate) { this.expectedInterestRate = expectedInterestRate; }

    public BigDecimal getOperatingExpenseRatio() { return operatingExpenseRatio; }
    public void setOperatingExpenseRatio(BigDecimal operatingExpenseRatio) { this.operatingExpenseRatio = operatingExpenseRatio; }

    public BigDecimal getDiscountSurchargeRate() { return discountSurchargeRate; }
    public void setDiscountSurchargeRate(BigDecimal discountSurchargeRate) { this.discountSurchargeRate = discountSurchargeRate; }
}
