package model.underwriting;

import enums.AccountingStatus;
import enums.ReinsuranceMethod;
import enums.SettlementMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 재보험 도메인 모델 — 재보험 계약 및 정산 정보 관리
public class Reinsurance {

    private String contractId;
    private String reinsurerName;
    private ReinsuranceMethod reinsuranceMethod;
    private float reinsuranceRate;
    private float cessionRate;
    private float retentionRate;
    private BigDecimal reinsurancePremium;
    private BigDecimal settlementAmount;
    private SettlementMethod settlementMethod;
    private LocalDateTime accountingDate;
    private LocalDate expectedSettlementDate;
    private AccountingStatus accountingStatus;

    public Reinsurance() {}

    // 재보험 기본 정보로 초기화
    public Reinsurance(String contractId, String reinsurerName, ReinsuranceMethod method,
                       float reinsuranceRate, BigDecimal reinsurancePremium) {
        this.contractId = contractId;
        this.reinsurerName = reinsurerName;
        this.reinsuranceMethod = method;
        this.reinsuranceRate = reinsuranceRate;
        this.reinsurancePremium = reinsurancePremium;
        this.accountingStatus = AccountingStatus.PENDING;
    }

    // 재보험료 산출
    public void calculatePremium() {}

    // 재보험료 회계 계상 — 계상 후 상태 반환
    public AccountingStatus recognizeAccounting() {
        this.accountingDate = LocalDateTime.now();
        this.accountingStatus = AccountingStatus.RECOGNIZED;
        return this.accountingStatus;
    }

    // 정산 일정 등록
    public void registerSettlementSchedule() {}

    // 재보험 조건 설정 — 설정 가능 여부 반환
    public boolean setConditions() {
        return reinsurerName != null && reinsuranceRate > 0;
    }

    public String getContractId() { return contractId; }
    public String getReinsurerName() { return reinsurerName; }
    public ReinsuranceMethod getReinsuranceMethod() { return reinsuranceMethod; }
    public float getReinsuranceRate() { return reinsuranceRate; }
    public float getCessionRate() { return cessionRate; }
    public float getRetentionRate() { return retentionRate; }
    public BigDecimal getReinsurancePremium() { return reinsurancePremium; }
    public BigDecimal getSettlementAmount() { return settlementAmount; }
    public SettlementMethod getSettlementMethod() { return settlementMethod; }
    public LocalDateTime getAccountingDate() { return accountingDate; }
    public LocalDate getExpectedSettlementDate() { return expectedSettlementDate; }
    public AccountingStatus getAccountingStatus() { return accountingStatus; }

    public void setContractId(String s) { this.contractId = s; }
    public void setReinsurerName(String s) { this.reinsurerName = s; }
    public void setReinsuranceMethod(ReinsuranceMethod m) { this.reinsuranceMethod = m; }
    public void setReinsuranceRate(float v) { this.reinsuranceRate = v; }
    public void setCessionRate(float v) { this.cessionRate = v; }
    public void setRetentionRate(float v) { this.retentionRate = v; }
    public void setReinsurancePremium(BigDecimal v) { this.reinsurancePremium = v; }
    public void setSettlementAmount(BigDecimal v) { this.settlementAmount = v; }
    public void setSettlementMethod(SettlementMethod m) { this.settlementMethod = m; }
    public void setAccountingDate(LocalDateTime t) { this.accountingDate = t; }
    public void setExpectedSettlementDate(LocalDate d) { this.expectedSettlementDate = d; }
    public void setAccountingStatus(AccountingStatus s) { this.accountingStatus = s; }

    @Override
    public String toString() {
        return "Reinsurance{reinsurer='" + reinsurerName + "', rate=" + reinsuranceRate
                + "%, premium=" + reinsurancePremium + ", accounting=" + accountingStatus + "}";
    }
}
