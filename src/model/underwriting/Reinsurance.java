package model.underwriting;

import enums.AccountingStatus;
import enums.ReinsuranceMethod;
import enums.SettlementMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 재보험 도메인 모델 — 재보험 계약 및 정산 정보 관리
public class Reinsurance {

    private LocalDateTime accountingDate;
    private float cessionRate;
    private String contractId;
    private LocalDate expectedSettlementDate;
    private ReinsuranceMethod reinsuranceMethod;
    private BigDecimal reinsurancePremium;
    private float reinsuranceRate;
    private String reinsurerName;
    private float retentionRate;
    private BigDecimal settlementAmount;
    private SettlementMethod settlementMethod;

    // DB 전환을 위해 추가된 필드 — 한 계약에 여러 재보험 처리가 가능하므로 별도 PK.
    private String reinsuranceId;

    public Reinsurance() {}

    public Reinsurance(String contractId, String reinsurerName, ReinsuranceMethod reinsuranceMethod,
                       float reinsuranceRate, float cessionRate, float retentionRate,
                       BigDecimal reinsurancePremium, BigDecimal settlementAmount,
                       SettlementMethod settlementMethod, LocalDate expectedSettlementDate,
                       LocalDateTime accountingDate) {
        this.contractId = contractId;
        this.reinsurerName = reinsurerName;
        this.reinsuranceMethod = reinsuranceMethod;
        this.reinsuranceRate = reinsuranceRate;
        this.cessionRate = cessionRate;
        this.retentionRate = retentionRate;
        this.reinsurancePremium = reinsurancePremium;
        this.settlementAmount = settlementAmount;
        this.settlementMethod = settlementMethod;
        this.expectedSettlementDate = expectedSettlementDate;
        this.accountingDate = accountingDate;
    }

    // 보험료 계산
    public void calculatePremium() {}

    // 회계 인식 — 회계 상태 반환
    public AccountingStatus recognizeAccounting() { return AccountingStatus.RECOGNIZED; }

    // 청산 일정 등록
    public void registerSettlementSchedule() {}

    // 재보험 조건 설정 — 성공 여부 반환
    public boolean setConditions() { return true; }

    public LocalDateTime    getAccountingDate()                     { return accountingDate; }
    public void             setAccountingDate(LocalDateTime v)      { this.accountingDate = v; }
    public float            getCessionRate()                        { return cessionRate; }
    public void             setCessionRate(float v)                 { this.cessionRate = v; }
    public String           getContractId()                         { return contractId; }
    public void             setContractId(String v)                 { this.contractId = v; }
    public LocalDate        getExpectedSettlementDate()             { return expectedSettlementDate; }
    public void             setExpectedSettlementDate(LocalDate v)  { this.expectedSettlementDate = v; }
    public ReinsuranceMethod getReinsuranceMethod()                 { return reinsuranceMethod; }
    public void             setReinsuranceMethod(ReinsuranceMethod v){ this.reinsuranceMethod = v; }
    public BigDecimal       getReinsurancePremium()                 { return reinsurancePremium; }
    public void             setReinsurancePremium(BigDecimal v)     { this.reinsurancePremium = v; }
    public float            getReinsuranceRate()                    { return reinsuranceRate; }
    public void             setReinsuranceRate(float v)             { this.reinsuranceRate = v; }
    public String           getReinsurerName()                      { return reinsurerName; }
    public void             setReinsurerName(String v)              { this.reinsurerName = v; }
    public float            getRetentionRate()                      { return retentionRate; }
    public void             setRetentionRate(float v)               { this.retentionRate = v; }
    public BigDecimal       getSettlementAmount()                   { return settlementAmount; }
    public void             setSettlementAmount(BigDecimal v)       { this.settlementAmount = v; }
    public SettlementMethod getSettlementMethod()                   { return settlementMethod; }
    public void             setSettlementMethod(SettlementMethod v) { this.settlementMethod = v; }
    public String           getReinsuranceId()                      { return reinsuranceId; }
    public void             setReinsuranceId(String v)              { this.reinsuranceId = v; }

    @Override
    public String toString() {
        return "Reinsurance{contractId='" + contractId + "', reinsurerName='" + reinsurerName + "', reinsuranceRate=" + reinsuranceRate + "}";
    }
}
