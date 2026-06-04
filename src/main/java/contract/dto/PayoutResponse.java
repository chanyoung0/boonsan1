package contract.dto;

import enums.CalculationBasis;
import enums.PaymentType;
import enums.PayoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PayoutResponse {

    private String payoutId;
    private String policyNumber;
    private CalculationBasis calculationBasis;
    private PaymentType paymentType;
    private BigDecimal paidPremiumAmount;
    private BigDecimal refundRate;
    private BigDecimal calculatedAmount;
    private String deductionItem;
    private BigDecimal deductionAmount;
    private BigDecimal finalPaymentAmount;
    private String processor;
    private PayoutStatus payoutStatus;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;

    public String getPayoutId() { return payoutId; }
    public void setPayoutId(String payoutId) { this.payoutId = payoutId; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public CalculationBasis getCalculationBasis() { return calculationBasis; }
    public void setCalculationBasis(CalculationBasis calculationBasis) { this.calculationBasis = calculationBasis; }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    public BigDecimal getPaidPremiumAmount() { return paidPremiumAmount; }
    public void setPaidPremiumAmount(BigDecimal paidPremiumAmount) { this.paidPremiumAmount = paidPremiumAmount; }

    public BigDecimal getRefundRate() { return refundRate; }
    public void setRefundRate(BigDecimal refundRate) { this.refundRate = refundRate; }

    public BigDecimal getCalculatedAmount() { return calculatedAmount; }
    public void setCalculatedAmount(BigDecimal calculatedAmount) { this.calculatedAmount = calculatedAmount; }

    public String getDeductionItem() { return deductionItem; }
    public void setDeductionItem(String deductionItem) { this.deductionItem = deductionItem; }

    public BigDecimal getDeductionAmount() { return deductionAmount; }
    public void setDeductionAmount(BigDecimal deductionAmount) { this.deductionAmount = deductionAmount; }

    public BigDecimal getFinalPaymentAmount() { return finalPaymentAmount; }
    public void setFinalPaymentAmount(BigDecimal finalPaymentAmount) { this.finalPaymentAmount = finalPaymentAmount; }

    public String getProcessor() { return processor; }
    public void setProcessor(String processor) { this.processor = processor; }

    public PayoutStatus getPayoutStatus() { return payoutStatus; }
    public void setPayoutStatus(PayoutStatus payoutStatus) { this.payoutStatus = payoutStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
