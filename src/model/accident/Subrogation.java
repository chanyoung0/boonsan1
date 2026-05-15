package model.accident;

import enums.SubrogationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 구상권 도메인 모델 — 보험금 지급 후 제3자 구상 처리 정보 관리
public class Subrogation {

    private String subrogationId;
    private String offenderName;
    private String offenderContact;
    private float faultRatio;
    private BigDecimal paymentAmount;
    private String depositAccount;
    private LocalDateTime paymentDeadline;
    private SubrogationStatus subrogationStatus;
    private InsurancePayment insurancePayment;

    public Subrogation() {}

    // 구상 기본 정보로 초기화
    public Subrogation(String subrogationId, String offenderName, float faultRatio, BigDecimal paymentAmount) {
        this.subrogationId = subrogationId;
        this.offenderName = offenderName;
        this.faultRatio = faultRatio;
        this.paymentAmount = paymentAmount;
        this.subrogationStatus = SubrogationStatus.IN_PROGRESS;
    }

    // 입금 확인
    public void confirmDeposit() {}

    // 구상 문서 생성
    public void generateSubrogationDocument() {}

    // 지급 상세 조회 — 원 보험금 지급 객체 반환
    public InsurancePayment retrievePaymentDetails() {
        return insurancePayment;
    }

    // 구상 청구 발송
    public void sendClaim() {}

    public String getSubrogationId() { return subrogationId; }
    public String getOffenderName() { return offenderName; }
    public String getOffenderContact() { return offenderContact; }
    public float getFaultRatio() { return faultRatio; }
    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public String getDepositAccount() { return depositAccount; }
    public LocalDateTime getPaymentDeadline() { return paymentDeadline; }
    public SubrogationStatus getSubrogationStatus() { return subrogationStatus; }
    public InsurancePayment getInsurancePayment() { return insurancePayment; }

    public void setSubrogationId(String s) { this.subrogationId = s; }
    public void setOffenderName(String s) { this.offenderName = s; }
    public void setOffenderContact(String s) { this.offenderContact = s; }
    public void setFaultRatio(float v) { this.faultRatio = v; }
    public void setPaymentAmount(BigDecimal v) { this.paymentAmount = v; }
    public void setDepositAccount(String s) { this.depositAccount = s; }
    public void setPaymentDeadline(LocalDateTime t) { this.paymentDeadline = t; }
    public void setSubrogationStatus(SubrogationStatus s) { this.subrogationStatus = s; }
    public void setInsurancePayment(InsurancePayment p) { this.insurancePayment = p; }

    @Override
    public String toString() {
        return "Subrogation{id='" + subrogationId + "', offender='" + offenderName
                + "', amount=" + paymentAmount + ", status=" + subrogationStatus + "}";
    }
}
