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

    // DB 전환을 위해 추가된 필드.
    private String unpaidNoticeId;
    private String paymentCollectionId;

    public UnpaidNotice() {}

    public UnpaidNotice(BigDecimal unpaidAmount, LocalDateTime dueDate,
                        PaymentMethod paymentMethod, LocalDateTime sentAt) {
        this.unpaidAmount = unpaidAmount;
        this.dueDate = dueDate;
        this.paymentMethod = paymentMethod;
        this.sentAt = sentAt;
    }

    public void calculateUnpaidAmount() {}
    public void sendNotice()            {}

    public LocalDateTime  getDueDate()                    { return dueDate; }
    public void           setDueDate(LocalDateTime v)     { this.dueDate = v; }
    public PaymentMethod  getPaymentMethod()              { return paymentMethod; }
    public void           setPaymentMethod(PaymentMethod v){ this.paymentMethod = v; }
    public LocalDateTime  getSentAt()                     { return sentAt; }
    public void           setSentAt(LocalDateTime v)      { this.sentAt = v; }
    public BigDecimal     getUnpaidAmount()               { return unpaidAmount; }
    public void           setUnpaidAmount(BigDecimal v)   { this.unpaidAmount = v; }
    public String         getUnpaidNoticeId()             { return unpaidNoticeId; }
    public void           setUnpaidNoticeId(String v)     { this.unpaidNoticeId = v; }
    public String         getPaymentCollectionId()        { return paymentCollectionId; }
    public void           setPaymentCollectionId(String v){ this.paymentCollectionId = v; }

    @Override
    public String toString() {
        return "UnpaidNotice{unpaidAmount=" + unpaidAmount + ", dueDate=" + dueDate + "}";
    }
}
