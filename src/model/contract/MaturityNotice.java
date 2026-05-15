package model.contract;

import enums.DeliveryMethod;

import java.time.LocalDateTime;

// 만기 안내 도메인 모델 — 계약 만기 도래 시 고객 안내 정보 관리
public class MaturityNotice {

    private LocalDateTime checkedAt;
    private DeliveryMethod deliveryMethod;
    private Boolean renewalIntention;
    private LocalDateTime sentAt;

    public MaturityNotice() {}

    // 만기 안내 발송 정보로 초기화
    public MaturityNotice(DeliveryMethod deliveryMethod, LocalDateTime sentAt) {
        this.deliveryMethod = deliveryMethod;
        this.sentAt = sentAt;
    }

    // 재계약 의사 확인
    public void checkRenewalIntention() {}

    // 만기 안내 발송
    public void sendNotice() {}

    public LocalDateTime getCheckedAt() { return checkedAt; }
    public DeliveryMethod getDeliveryMethod() { return deliveryMethod; }
    public Boolean getRenewalIntention() { return renewalIntention; }
    public LocalDateTime getSentAt() { return sentAt; }

    public void setCheckedAt(LocalDateTime t) { this.checkedAt = t; }
    public void setDeliveryMethod(DeliveryMethod m) { this.deliveryMethod = m; }
    public void setRenewalIntention(Boolean b) { this.renewalIntention = b; }
    public void setSentAt(LocalDateTime t) { this.sentAt = t; }

    @Override
    public String toString() {
        return "MaturityNotice{sentAt=" + sentAt + ", renewal=" + renewalIntention + "}";
    }
}
