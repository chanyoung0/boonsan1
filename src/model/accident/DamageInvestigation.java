package model.accident;

import enums.RequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 손해조사 도메인 모델 — 사고 손해 여부 및 손해 규모 판단 정보 관리
public class DamageInvestigation {

    private String investigationId;
    private String adjusterId;
    private LocalDateTime investigationAt;
    private float faultRatio;
    private BigDecimal medicalExpense;
    private BigDecimal lostIncome;
    private BigDecimal repairCost;
    private BigDecimal settlementAmount;
    private final List<OutsourceRequest> outsourceRequests = new ArrayList<>();
    private InsurancePayment insurancePayment;

    public DamageInvestigation() {}

    // 손해조사 기본 정보로 초기화
    public DamageInvestigation(String investigationId, String adjusterId, LocalDateTime investigationAt) {
        this.investigationId = investigationId;
        this.adjusterId = adjusterId;
        this.investigationAt = investigationAt;
    }

    // 손해액 산정
    public void assessDamageCost() {}

    // 외부 조사 위탁
    public void delegateInvestigation() {}

    // 사고 청구 반려
    public void rejectClaim() {}

    // 보험사기 조사 요청 — 요청 상태 반환
    public RequestStatus requestFraudInvestigation() {
        return RequestStatus.PENDING;
    }

    public String getInvestigationId() { return investigationId; }
    public String getAdjusterId() { return adjusterId; }
    public LocalDateTime getInvestigationAt() { return investigationAt; }
    public float getFaultRatio() { return faultRatio; }
    public BigDecimal getMedicalExpense() { return medicalExpense; }
    public BigDecimal getLostIncome() { return lostIncome; }
    public BigDecimal getRepairCost() { return repairCost; }
    public BigDecimal getSettlementAmount() { return settlementAmount; }
    public List<OutsourceRequest> getOutsourceRequests() { return outsourceRequests; }
    public InsurancePayment getInsurancePayment() { return insurancePayment; }

    public void setInvestigationId(String s) { this.investigationId = s; }
    public void setAdjusterId(String s) { this.adjusterId = s; }
    public void setInvestigationAt(LocalDateTime t) { this.investigationAt = t; }
    public void setFaultRatio(float v) { this.faultRatio = v; }
    public void setMedicalExpense(BigDecimal v) { this.medicalExpense = v; }
    public void setLostIncome(BigDecimal v) { this.lostIncome = v; }
    public void setRepairCost(BigDecimal v) { this.repairCost = v; }
    public void setSettlementAmount(BigDecimal v) { this.settlementAmount = v; }
    public void addOutsourceRequest(OutsourceRequest r) { this.outsourceRequests.add(r); }
    public void setInsurancePayment(InsurancePayment p) { this.insurancePayment = p; }

    @Override
    public String toString() {
        return "DamageInvestigation{id='" + investigationId + "', adjuster='" + adjusterId
                + "', settlement=" + settlementAmount + "}";
    }
}
