package model.contract;

import enums.CalculationBasis;
import enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 지급 결정 도메인 모델 — 보험금 지급 승인 및 산정 정보 관리
public class Payout {

    private LocalDateTime approvedAt;
    private BigDecimal calculatedAmount;
    private CalculationBasis calculationBasis;
    private String deductionItem;
    private BigDecimal finalPaymentAmount;
    private LocalDateTime paidAt;
    private PaymentType paymentType;
    private String processor;

    public Payout() {}

    // 지급 결정 기본 정보로 초기화
    public Payout(PaymentType paymentType, CalculationBasis calculationBasis,
                  BigDecimal calculatedAmount, BigDecimal finalPaymentAmount, String processor) {
        this.paymentType = paymentType;
        this.calculationBasis = calculationBasis;
        this.calculatedAmount = calculatedAmount;
        this.finalPaymentAmount = finalPaymentAmount;
        this.processor = processor;
    }

    // 지급 승인
    public void approvePayment() {}

    // 지급액 산정
    public void calculatePayment() {}

    // 지급 취소
    public void cancelPayment() {}

    // 지급 처리
    public void processPayment() {}

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public BigDecimal getCalculatedAmount() { return calculatedAmount; }
    public CalculationBasis getCalculationBasis() { return calculationBasis; }
    public String getDeductionItem() { return deductionItem; }
    public BigDecimal getFinalPaymentAmount() { return finalPaymentAmount; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public PaymentType getPaymentType() { return paymentType; }
    public String getProcessor() { return processor; }

    public void setApprovedAt(LocalDateTime t) { this.approvedAt = t; }
    public void setCalculatedAmount(BigDecimal v) { this.calculatedAmount = v; }
    public void setCalculationBasis(CalculationBasis c) { this.calculationBasis = c; }
    public void setDeductionItem(String s) { this.deductionItem = s; }
    public void setFinalPaymentAmount(BigDecimal v) { this.finalPaymentAmount = v; }
    public void setPaidAt(LocalDateTime t) { this.paidAt = t; }
    public void setPaymentType(PaymentType t) { this.paymentType = t; }
    public void setProcessor(String s) { this.processor = s; }

    @Override
    public String toString() {
        return "Payout{type=" + paymentType + ", amount=" + finalPaymentAmount
                + ", paidAt=" + paidAt + "}";
    }
}
