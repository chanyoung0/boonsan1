package model.contract;

import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 미납 안내 도메인 모델 — 보험료 미납 시 고객 안내 정보 관리
public class UnpaidNotice {

    private LocalDateTime dueDate;
    private LocalDateTime sentAt;
    private PaymentMethod paymentMethod;
    private BigDecimal unpaidAmount;

    public UnpaidNotice() {}

    // 미납 안내 기본 정보로 초기화
    public UnpaidNotice(LocalDateTime dueDate, BigDecimal unpaidAmount, PaymentMethod paymentMethod) {
        this.dueDate = dueDate;
        this.unpaidAmount = unpaidAmount;
        this.paymentMethod = paymentMethod;
    }

    // 미납 금액 계산
    public void calculateUnpaidAmount() {}

    // 미납 안내 발송
    public void sendNotice() {}

    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getSentAt() { return sentAt; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public BigDecimal getUnpaidAmount() { return unpaidAmount; }

    public void setDueDate(LocalDateTime t) { this.dueDate = t; }
    public void setSentAt(LocalDateTime t) { this.sentAt = t; }
    public void setPaymentMethod(PaymentMethod m) { this.paymentMethod = m; }
    public void setUnpaidAmount(BigDecimal v) { this.unpaidAmount = v; }

    @Override
    public String toString() {
        return "UnpaidNotice{due=" + dueDate + ", amount=" + unpaidAmount + ", sentAt=" + sentAt + "}";
    }
}
