package model.contract;

import enums.DeliveryMethod;

import java.time.LocalDateTime;

// 만기 안내 도메인 모델 — 계약 만기 안내 발송 및 재계약 의사 확인 정보 관리
public class MaturityNotice {

    private String noticeId;
    private LocalDateTime checkedAt;
    private DeliveryMethod deliveryMethod;
    private Boolean renewalIntention;
    private LocalDateTime sentAt;

    public MaturityNotice() {}

    public MaturityNotice(DeliveryMethod deliveryMethod, LocalDateTime sentAt) {
        this.deliveryMethod = deliveryMethod;
        this.sentAt = sentAt;
    }

    public void checkRenewalIntention() {}
    public void sendNotice()            {}

    public String         getNoticeId()                     { return noticeId; }
    public void           setNoticeId(String v)             { this.noticeId = v; }
    public LocalDateTime  getCheckedAt()                    { return checkedAt; }
    public void           setCheckedAt(LocalDateTime v)     { this.checkedAt = v; }
    public DeliveryMethod getDeliveryMethod()               { return deliveryMethod; }
    public void           setDeliveryMethod(DeliveryMethod v){ this.deliveryMethod = v; }
    public Boolean        getRenewalIntention()             { return renewalIntention; }
    public void           setRenewalIntention(Boolean v)    { this.renewalIntention = v; }
    public LocalDateTime  getSentAt()                       { return sentAt; }
    public void           setSentAt(LocalDateTime v)        { this.sentAt = v; }

    @Override
    public String toString() {
        return "MaturityNotice{deliveryMethod=" + deliveryMethod + ", renewalIntention=" + renewalIntention + "}";
    }
}
