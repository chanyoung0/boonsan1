package model.accident;

import enums.SubrogationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 구상권 도메인 모델 — 보험금 지급 후 제3자 구상 처리 정보 관리
public class Subrogation {

    private String depositAccount;
    private float faultRatio;
    private String offenderContact;
    private String offenderName;
    private BigDecimal paymentAmount;
    private LocalDateTime paymentDeadline;
    private String subrogationId;
    private SubrogationStatus subrogationStatus;

    public Subrogation() {}

    public Subrogation(String subrogationId, String offenderName, String offenderContact,
                       float faultRatio, BigDecimal paymentAmount,
                       LocalDateTime paymentDeadline, String depositAccount,
                       SubrogationStatus subrogationStatus) {
        this.subrogationId = subrogationId;
        this.offenderName = offenderName;
        this.offenderContact = offenderContact;
        this.faultRatio = faultRatio;
        this.paymentAmount = paymentAmount;
        this.paymentDeadline = paymentDeadline;
        this.depositAccount = depositAccount;
        this.subrogationStatus = subrogationStatus;
    }

    // 입금 확인
    public void confirmDeposit() {}

    // 구상 문서 생성
    public void generateSubrogationDocument() {}

    // 지급 상세 조회 — 관련 InsurancePayment 반환
    public InsurancePayment retrievePaymentDetails() { return null; }

    // 구상 청구 발송
    public void sendClaim() {}

    public String           getDepositAccount()                     { return depositAccount; }
    public void             setDepositAccount(String v)             { this.depositAccount = v; }
    public float            getFaultRatio()                         { return faultRatio; }
    public void             setFaultRatio(float v)                  { this.faultRatio = v; }
    public String           getOffenderContact()                    { return offenderContact; }
    public void             setOffenderContact(String v)            { this.offenderContact = v; }
    public String           getOffenderName()                       { return offenderName; }
    public void             setOffenderName(String v)               { this.offenderName = v; }
    public BigDecimal       getPaymentAmount()                      { return paymentAmount; }
    public void             setPaymentAmount(BigDecimal v)          { this.paymentAmount = v; }
    public LocalDateTime    getPaymentDeadline()                    { return paymentDeadline; }
    public void             setPaymentDeadline(LocalDateTime v)     { this.paymentDeadline = v; }
    public String           getSubrogationId()                      { return subrogationId; }
    public void             setSubrogationId(String v)              { this.subrogationId = v; }
    public SubrogationStatus getSubrogationStatus()                 { return subrogationStatus; }
    public void             setSubrogationStatus(SubrogationStatus v){ this.subrogationStatus = v; }

    @Override
    public String toString() {
        return "Subrogation{subrogationId='" + subrogationId + "', offenderName='" + offenderName + "', subrogationStatus=" + subrogationStatus + "}";
    }
}
