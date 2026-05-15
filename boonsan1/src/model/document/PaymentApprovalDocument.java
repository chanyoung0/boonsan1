package model.document;

import enums.ApprovalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentApprovalDocument extends Document {

    private ApprovalStatus approvalStatus;
    private LocalDateTime approvedAt;
    private String approvedEmployeeNo;
    private String damageAdequacyOpinion;
    private String faultRatioOpinion;
    private BigDecimal lostIncomeAmount;
    private BigDecimal medicalExpenseAmount;
    private BigDecimal repairCostAmount;
    private BigDecimal settlementAmount;

    public void addOpinion() {}

    public void createDraft() {}

    @Override
    public void save() {}

    public void submitForApproval() {}
}
