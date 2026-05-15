package model.accident;

import enums.PaymentStatus;
import model.document.PaymentApprovalDocument;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 보험금 지급 도메인 모델 — 손해조사 결과 기반 보험금 지급 처리 정보 관리
public class InsurancePayment {

    private BigDecimal finalLostIncome;
    private BigDecimal finalMedicalExpense;
    private BigDecimal finalRepairCost;
    private BigDecimal finalSettlementAmount;
    private LocalDateTime paidAt;
    private String paymentAccount;
    private String paymentId;
    private PaymentStatus paymentStatus;
    private String processorEmployeeNo;
    private BigDecimal retentionEstimate;

    private Objection objection;
    private Subrogation subrogation;
    private PaymentApprovalDocument paymentApprovalDocument;

    public InsurancePayment() {}

    public InsurancePayment(String paymentId, String paymentAccount, String processorEmployeeNo,
                            BigDecimal finalSettlementAmount, BigDecimal finalRepairCost,
                            BigDecimal finalMedicalExpense, BigDecimal finalLostIncome,
                            BigDecimal retentionEstimate, PaymentStatus paymentStatus) {
        this.paymentId = paymentId;
        this.paymentAccount = paymentAccount;
        this.processorEmployeeNo = processorEmployeeNo;
        this.finalSettlementAmount = finalSettlementAmount;
        this.finalRepairCost = finalRepairCost;
        this.finalMedicalExpense = finalMedicalExpense;
        this.finalLostIncome = finalLostIncome;
        this.retentionEstimate = retentionEstimate;
        this.paymentStatus = paymentStatus;
    }

    // 사건 종결
    public void closeCase() {
        this.paymentStatus = PaymentStatus.CLOSED;
    }

    // 수령 확인
    public void confirmReceipt() {
        if (paymentAccount == null || paymentAccount.isEmpty())
            throw new IllegalStateException("지급 계좌가 설정되지 않았습니다.");
        this.paymentStatus = PaymentStatus.PAID;
        if (this.paidAt == null) this.paidAt = LocalDateTime.now();
    }

    // 지급 기록 생성 — PaymentApprovalDocument 반환
    public PaymentApprovalDocument generatePaymentRecord() { return paymentApprovalDocument; }

    // 구상권 등록
    public void registerSubrogation() {
        if (subrogation == null)
            throw new IllegalStateException("구상권 정보가 없습니다.");
    }

    // 지급 알림 발송
    public void sendNotification() {
        if (paymentAccount == null || paymentAccount.isEmpty())
            throw new IllegalStateException("수신 계좌가 설정되지 않았습니다.");
    }

    // 보험금 이체 — 이체 처리 결과 상태 반환
    public PaymentStatus transfer() {
        this.paidAt = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PAID;
        return paymentStatus;
    }

    public BigDecimal              getFinalLostIncome()                          { return finalLostIncome; }
    public void                    setFinalLostIncome(BigDecimal v)              { this.finalLostIncome = v; }
    public BigDecimal              getFinalMedicalExpense()                      { return finalMedicalExpense; }
    public void                    setFinalMedicalExpense(BigDecimal v)          { this.finalMedicalExpense = v; }
    public BigDecimal              getFinalRepairCost()                          { return finalRepairCost; }
    public void                    setFinalRepairCost(BigDecimal v)              { this.finalRepairCost = v; }
    public BigDecimal              getFinalSettlementAmount()                    { return finalSettlementAmount; }
    public void                    setFinalSettlementAmount(BigDecimal v)        { this.finalSettlementAmount = v; }
    public LocalDateTime           getPaidAt()                                   { return paidAt; }
    public void                    setPaidAt(LocalDateTime v)                    { this.paidAt = v; }
    public String                  getPaymentAccount()                           { return paymentAccount; }
    public void                    setPaymentAccount(String v)                   { this.paymentAccount = v; }
    public String                  getPaymentId()                                { return paymentId; }
    public void                    setPaymentId(String v)                        { this.paymentId = v; }
    public PaymentStatus           getPaymentStatus()                            { return paymentStatus; }
    public void                    setPaymentStatus(PaymentStatus v)             { this.paymentStatus = v; }
    public String                  getProcessorEmployeeNo()                      { return processorEmployeeNo; }
    public void                    setProcessorEmployeeNo(String v)              { this.processorEmployeeNo = v; }
    public BigDecimal              getRetentionEstimate()                        { return retentionEstimate; }
    public void                    setRetentionEstimate(BigDecimal v)            { this.retentionEstimate = v; }
    public Objection               getObjection()                                { return objection; }
    public void                    setObjection(Objection v)                     { this.objection = v; }
    public Subrogation             getSubrogation()                              { return subrogation; }
    public void                    setSubrogation(Subrogation v)                 { this.subrogation = v; }
    public PaymentApprovalDocument getPaymentApprovalDocument()                  { return paymentApprovalDocument; }
    public void                    setPaymentApprovalDocument(PaymentApprovalDocument v){ this.paymentApprovalDocument = v; }

    @Override
    public String toString() {
        return "InsurancePayment{paymentId='" + paymentId + "', paymentStatus=" + paymentStatus + ", finalSettlementAmount=" + finalSettlementAmount + "}";
    }
}
