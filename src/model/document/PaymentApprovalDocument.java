package model.document;

import enums.ApprovalStatus;
import enums.DocumentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 지급 품의서 도메인 모델 — 보험금 지급 승인을 위한 결재 문서 관리
public class PaymentApprovalDocument extends Document {

    private ApprovalStatus approvalStatus;
    private LocalDateTime approvedAt;
    private String approverEmployeeNo;
    private String damageAdequacyOpinion;
    private BigDecimal lostIncomeAmount;
    private BigDecimal medicalExpenseAmount;
    private BigDecimal repairCostAmount;
    private BigDecimal settlementAmount;
    private String remarks;

    public PaymentApprovalDocument() {}

    // 지급 품의서 결재 정보로 초기화
    public PaymentApprovalDocument(String documentId, LocalDateTime createdAt, DocumentStatus status,
                                   ApprovalStatus approvalStatus, BigDecimal settlementAmount) {
        super(documentId, createdAt, status);
        this.approvalStatus = approvalStatus;
        this.settlementAmount = settlementAmount;
    }

    // 결재 의견 작성
    public void addOpinion() {}

    // 결재 초안 생성
    public void createDraft() {}

    @Override
    public void save() {}

    // 결재 상신
    public void submitForApproval() {}

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getApproverEmployeeNo() { return approverEmployeeNo; }
    public String getDamageAdequacyOpinion() { return damageAdequacyOpinion; }
    public BigDecimal getLostIncomeAmount() { return lostIncomeAmount; }
    public BigDecimal getMedicalExpenseAmount() { return medicalExpenseAmount; }
    public BigDecimal getRepairCostAmount() { return repairCostAmount; }
    public BigDecimal getSettlementAmount() { return settlementAmount; }
    public String getRemarks() { return remarks; }

    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public void setApproverEmployeeNo(String approverEmployeeNo) { this.approverEmployeeNo = approverEmployeeNo; }
    public void setDamageAdequacyOpinion(String s) { this.damageAdequacyOpinion = s; }
    public void setLostIncomeAmount(BigDecimal v) { this.lostIncomeAmount = v; }
    public void setMedicalExpenseAmount(BigDecimal v) { this.medicalExpenseAmount = v; }
    public void setRepairCostAmount(BigDecimal v) { this.repairCostAmount = v; }
    public void setSettlementAmount(BigDecimal v) { this.settlementAmount = v; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public String toString() {
        return "PaymentApprovalDocument{id='" + documentId + "', approvalStatus=" + approvalStatus
                + ", settlement=" + settlementAmount + "}";
    }
}
