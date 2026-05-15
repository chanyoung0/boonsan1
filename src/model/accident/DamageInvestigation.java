package model.accident;

import enums.RequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 손해조사 도메인 모델 — 사고 손해 여부 및 손해 규모 판단 정보 관리
public class DamageInvestigation {

    private String adjusterId;
    private float faultRatio;
    private LocalDateTime investigationAt;
    private String investigationId;
    private BigDecimal lostIncome;
    private BigDecimal medicalExpense;
    private BigDecimal repairCost;
    private BigDecimal settlementAmount;

    private OutsourceRequest outsourceRequest;
    private InsurancePayment insurancePayment;

    public DamageInvestigation() {}

    public DamageInvestigation(String investigationId, String adjusterId, float faultRatio,
                               BigDecimal repairCost, BigDecimal medicalExpense,
                               BigDecimal lostIncome, BigDecimal settlementAmount,
                               LocalDateTime investigationAt) {
        this.investigationId = investigationId;
        this.adjusterId = adjusterId;
        this.faultRatio = faultRatio;
        this.repairCost = repairCost;
        this.medicalExpense = medicalExpense;
        this.lostIncome = lostIncome;
        this.settlementAmount = settlementAmount;
        this.investigationAt = investigationAt;
    }

    // 손해액 산정
    public void assessDamageCost() {}

    // 외부 조사 위탁
    public void delegateInvestigation() {}

    // 사고 청구 반려
    public void rejectClaim() {}

    // 보험사기 조사 요청 — 요청 결과 상태 반환
    public RequestStatus requestFraudInvestigation() { return RequestStatus.PENDING; }

    public String          getAdjusterId()                      { return adjusterId; }
    public void            setAdjusterId(String v)              { this.adjusterId = v; }
    public float           getFaultRatio()                      { return faultRatio; }
    public void            setFaultRatio(float v)               { this.faultRatio = v; }
    public LocalDateTime   getInvestigationAt()                 { return investigationAt; }
    public void            setInvestigationAt(LocalDateTime v)  { this.investigationAt = v; }
    public String          getInvestigationId()                 { return investigationId; }
    public void            setInvestigationId(String v)         { this.investigationId = v; }
    public BigDecimal      getLostIncome()                      { return lostIncome; }
    public void            setLostIncome(BigDecimal v)          { this.lostIncome = v; }
    public BigDecimal      getMedicalExpense()                  { return medicalExpense; }
    public void            setMedicalExpense(BigDecimal v)      { this.medicalExpense = v; }
    public BigDecimal      getRepairCost()                      { return repairCost; }
    public void            setRepairCost(BigDecimal v)          { this.repairCost = v; }
    public BigDecimal      getSettlementAmount()                { return settlementAmount; }
    public void            setSettlementAmount(BigDecimal v)    { this.settlementAmount = v; }
    public OutsourceRequest getOutsourceRequest()               { return outsourceRequest; }
    public void            setOutsourceRequest(OutsourceRequest v){ this.outsourceRequest = v; }
    public InsurancePayment getInsurancePayment()               { return insurancePayment; }
    public void            setInsurancePayment(InsurancePayment v){ this.insurancePayment = v; }

    @Override
    public String toString() {
        return "DamageInvestigation{investigationId='" + investigationId + "', settlementAmount=" + settlementAmount + "}";
    }
}
