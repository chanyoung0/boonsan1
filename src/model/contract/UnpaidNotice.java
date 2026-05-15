package model.contract;

import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 미납 안내 도메인 모델 — 미납 보험료 안내장 발송 정보 관리
public class UnpaidNotice {

    private LocalDateTime dueDate;
    private PaymentMethod paymentMethod;
    private LocalDateTime sentAt;
    private BigDecimal unpaidAmount;

    public UnpaidNotice() {}

    public UnpaidNotice(BigDecimal unpaidAmount, LocalDateTime dueDate,
                        PaymentMethod paymentMethod, LocalDateTime sentAt) {
        this.unpaidAmount = unpaidAmount;
        this.dueDate = dueDate;
        this.paymentMethod = paymentMethod;
        this.sentAt = sentAt;
    }

    // 미납액 계산
    public void calculateUnpaidAmount() {
        if (unpaidAmount == null)
            throw new IllegalStateException("미납액이 설정되지 않았습니다.");
    }

    // 미납 안내 발송
    public void sendNotice() {
        if (unpaidAmount == null || unpaidAmount.compareTo(java.math.BigDecimal.ZERO) <= 0)
            throw new IllegalStateException("미납액이 유효하지 않습니다.");
        if (paymentMethod == null)
            throw new IllegalStateException("납부 방법이 설정되지 않았습니다.");
        if (this.sentAt == null) this.sentAt = java.time.LocalDateTime.now();
    }

    public LocalDateTime  getDueDate()                    { return dueDate; }
    public void           setDueDate(LocalDateTime v)     { this.dueDate = v; }
    public PaymentMethod  getPaymentMethod()              { return paymentMethod; }
    public void           setPaymentMethod(PaymentMethod v){ this.paymentMethod = v; }
    public LocalDateTime  getSentAt()                     { return sentAt; }
    public void           setSentAt(LocalDateTime v)      { this.sentAt = v; }
    public BigDecimal     getUnpaidAmount()               { return unpaidAmount; }
    public void           setUnpaidAmount(BigDecimal v)   { this.unpaidAmount = v; }

    @Override
    public String toString() {
        return "UnpaidNotice{unpaidAmount=" + unpaidAmount + ", dueDate=" + dueDate + "}";
    }
}
