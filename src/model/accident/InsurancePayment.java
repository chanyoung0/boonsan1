package model.accident;

import enums.PaymentStatus;
import model.document.PaymentApprovalDocument;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 보험금 지급 도메인 모델 — 손해조사 결과 기반 보험금 지급 처리 정보 관리
public class InsurancePayment {

    private String paymentId;
    private BigDecimal finalSettlementAmount;
    private BigDecimal finalLostIncome;
    private BigDecimal finalMedicalExpense;
    private BigDecimal finalRepairCost;
    private BigDecimal retentionEstimate;
    private String paymentAccount;
    private LocalDateTime paidAt;
    private PaymentStatus paymentStatus;
    private String processorEmployeeNo;
    private final List<Objection> objections = new ArrayList<>();
    private final List<Subrogation> subrogations = new ArrayList<>();
    private final List<PaymentApprovalDocument> paymentApprovalDocuments = new ArrayList<>();

    public InsurancePayment() {}

    // 보험금 지급 기본 정보로 초기화
    public InsurancePayment(String paymentId, BigDecimal finalSettlementAmount, String paymentAccount, String processorEmployeeNo) {
        this.paymentId = paymentId;
        this.finalSettlementAmount = finalSettlementAmount;
        this.paymentAccount = paymentAccount;
        this.processorEmployeeNo = processorEmployeeNo;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    // 사건 종결
    public void closeCase() {}

    // 수령 확인
    public void confirmReceipt() {}

    // 지급 기록 생성 — 결재 문서 반환
    public PaymentApprovalDocument generatePaymentRecord() {
        PaymentApprovalDocument doc = new PaymentApprovalDocument();
        doc.setSettlementAmount(this.finalSettlementAmount);
        this.paymentApprovalDocuments.add(doc);
        return doc;
    }

    // 구상권 등록
    public void registerSubrogation() {}

    // 지급 알림 발송
    public void sendNotification() {}

    // 보험금 이체 — 이체 후 지급 상태 반환
    public PaymentStatus transfer() {
        this.paidAt = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PAID;
        return this.paymentStatus;
    }

    public String getPaymentId() { return paymentId; }
    public BigDecimal getFinalSettlementAmount() { return finalSettlementAmount; }
    public BigDecimal getFinalLostIncome() { return finalLostIncome; }
    public BigDecimal getFinalMedicalExpense() { return finalMedicalExpense; }
    public BigDecimal getFinalRepairCost() { return finalRepairCost; }
    public BigDecimal getRetentionEstimate() { return retentionEstimate; }
    public String getPaymentAccount() { return paymentAccount; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getProcessorEmployeeNo() { return processorEmployeeNo; }
    public List<Objection> getObjections() { return objections; }
    public List<Subrogation> getSubrogations() { return subrogations; }
    public List<PaymentApprovalDocument> getPaymentApprovalDocuments() { return paymentApprovalDocuments; }

    public void setPaymentId(String s) { this.paymentId = s; }
    public void setFinalSettlementAmount(BigDecimal v) { this.finalSettlementAmount = v; }
    public void setFinalLostIncome(BigDecimal v) { this.finalLostIncome = v; }
    public void setFinalMedicalExpense(BigDecimal v) { this.finalMedicalExpense = v; }
    public void setFinalRepairCost(BigDecimal v) { this.finalRepairCost = v; }
    public void setRetentionEstimate(BigDecimal v) { this.retentionEstimate = v; }
    public void setPaymentAccount(String s) { this.paymentAccount = s; }
    public void setPaidAt(LocalDateTime t) { this.paidAt = t; }
    public void setPaymentStatus(PaymentStatus s) { this.paymentStatus = s; }
    public void setProcessorEmployeeNo(String s) { this.processorEmployeeNo = s; }
    public void addObjection(Objection o) { this.objections.add(o); }
    public void addSubrogation(Subrogation s) { this.subrogations.add(s); }
    public void addPaymentApprovalDocument(PaymentApprovalDocument d) { this.paymentApprovalDocuments.add(d); }

    @Override
    public String toString() {
        return "InsurancePayment{id='" + paymentId + "', amount=" + finalSettlementAmount
                + ", status=" + paymentStatus + "}";
    }
}
