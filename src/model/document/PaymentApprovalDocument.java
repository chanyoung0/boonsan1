package model.document;

import enums.ApprovalStatus;
import enums.DocumentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 지급 승인 문서 도메인 모델 — 보험금 지급 품의 승인 문서 관리
public class PaymentApprovalDocument extends Document {

    private ApprovalStatus approvalStatus;
    private LocalDateTime approvedAt;
    private String approverEmployeeNo;
    private String damageAdequacyOpinion;
    private BigDecimal lostIncomeAmount;
    private BigDecimal medicalExpenseAmount;
    private String remarks;
    private BigDecimal repairCostAmount;
    private BigDecimal settlementAmount;

    public PaymentApprovalDocument() { super(); }

    public PaymentApprovalDocument(String documentId, DocumentStatus status, LocalDateTime createdAt,
                                   ApprovalStatus approvalStatus, String approverEmployeeNo,
                                   BigDecimal settlementAmount, BigDecimal repairCostAmount,
                                   BigDecimal medicalExpenseAmount, BigDecimal lostIncomeAmount,
                                   String damageAdequacyOpinion, String remarks) {
        super(documentId, status, createdAt);
        this.approvalStatus = approvalStatus;
        this.approverEmployeeNo = approverEmployeeNo;
        this.settlementAmount = settlementAmount;
        this.repairCostAmount = repairCostAmount;
        this.medicalExpenseAmount = medicalExpenseAmount;
        this.lostIncomeAmount = lostIncomeAmount;
        this.damageAdequacyOpinion = damageAdequacyOpinion;
        this.remarks = remarks;
    }

    public void addOpinion()        {}
    public void createDraft()       {}

    @Override
    public void save()              {}

    public void submitForApproval() {}

    public ApprovalStatus getApprovalStatus()                    { return approvalStatus; }
    public void           setApprovalStatus(ApprovalStatus v)    { this.approvalStatus = v; }
    public LocalDateTime  getApprovedAt()                        { return approvedAt; }
    public void           setApprovedAt(LocalDateTime v)         { this.approvedAt = v; }
    public String         getApproverEmployeeNo()                { return approverEmployeeNo; }
    public void           setApproverEmployeeNo(String v)        { this.approverEmployeeNo = v; }
    public String         getDamageAdequacyOpinion()             { return damageAdequacyOpinion; }
    public void           setDamageAdequacyOpinion(String v)     { this.damageAdequacyOpinion = v; }
    public BigDecimal     getLostIncomeAmount()                  { return lostIncomeAmount; }
    public void           setLostIncomeAmount(BigDecimal v)      { this.lostIncomeAmount = v; }
    public BigDecimal     getMedicalExpenseAmount()              { return medicalExpenseAmount; }
    public void           setMedicalExpenseAmount(BigDecimal v)  { this.medicalExpenseAmount = v; }
    public String         getRemarks()                           { return remarks; }
    public void           setRemarks(String v)                   { this.remarks = v; }
    public BigDecimal     getRepairCostAmount()                  { return repairCostAmount; }
    public void           setRepairCostAmount(BigDecimal v)      { this.repairCostAmount = v; }
    public BigDecimal     getSettlementAmount()                  { return settlementAmount; }
    public void           setSettlementAmount(BigDecimal v)      { this.settlementAmount = v; }

    @Override
    public String toString() {
        return "PaymentApprovalDocument{documentId='" + documentId + "', approvalStatus=" + approvalStatus + ", settlementAmount=" + settlementAmount + "}";
    }
}
