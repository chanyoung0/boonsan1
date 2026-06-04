package com.boonsan.domain.model.underwriting;

import com.boonsan.domain.enums.ReinsuranceMethod;
import com.boonsan.domain.enums.SettlementMethod;

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

    public void calculatePremium() {}

    public void recognizeAccounting() {}

    public void registerSettlementSchedule() {}

    public void setConditions() {}
}
