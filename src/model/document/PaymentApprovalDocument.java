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

    // 의견 추가
    public void addOpinion() {
        if (damageAdequacyOpinion == null || damageAdequacyOpinion.isEmpty())
            throw new IllegalStateException("손해 적정 의견이 없습니다.");
    }

    // 초안 작성
    public void createDraft() {
        if (documentId == null || documentId.isEmpty())
            this.documentId = "PAD-" + System.currentTimeMillis();
        this.approvalStatus = ApprovalStatus.DRAFT;
        this.status = DocumentStatus.DRAFT;
        if (this.createdAt == null) this.createdAt = java.time.LocalDateTime.now();
    }

    @Override
    public void save() {
        if (documentId == null || documentId.isEmpty())
            throw new IllegalStateException("문서 ID가 없습니다.");
        this.status = DocumentStatus.SUBMITTED;
    }

    // 승인 요청 제출
    public void submitForApproval() {
        if (settlementAmount == null)
            throw new IllegalStateException("지급금액이 설정되지 않았습니다.");
        if (approverEmployeeNo == null || approverEmployeeNo.isEmpty())
            throw new IllegalStateException("승인자 사번이 없습니다.");
        this.approvalStatus = ApprovalStatus.PENDING_APPROVAL;
    }

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
