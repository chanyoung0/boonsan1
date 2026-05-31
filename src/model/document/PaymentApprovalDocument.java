package model.document;

import enums.ApprovalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 지급 승인 문서 도메인 모델 — 보험금 지급 품의 승인 문서 관리
public class PaymentApprovalDocument extends Document {

    private ApprovalStatus approvalStatus;
    private LocalDateTime approvedAt;
    private String approverEmployeeNo;
    private String damageAdequacyOpinion;
    private String faultRatioOpinion;
    private BigDecimal lostIncomeAmount;
    private BigDecimal medicalExpenseAmount;
    private String remarks;
    private BigDecimal repairCostAmount;
    private BigDecimal settlementAmount;

    public void addOpinion() {}

    public void createDraft() {}

    @Override
    public void save() {}

    public void submitForApproval() {}
}
