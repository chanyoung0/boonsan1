package model.contract;

import enums.ReinstatementReason;
import enums.UnderwritingResultType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 부활 도메인 모델 — 실효 계약 부활 신청 및 처리 정보 관리
public class Reinstatement {

    private String reinstatementId;
    private String policyNumber;
    private LocalDateTime appliedAt;
    private LocalDateTime desiredDate;
    private boolean hasHealthChanged;
    private LocalDate lastPaidDate;
    private LocalDateTime processedAt;
    private ReinstatementReason reinstatementReason;
    private String reinstatementStatus;
    private BigDecimal unpaidPremium;
    private UnderwritingResultType underwritingResult;

    public Reinstatement() {}

    public Reinstatement(ReinstatementReason reinstatementReason, BigDecimal unpaidPremium,
                         LocalDateTime appliedAt, LocalDateTime desiredDate,
                         LocalDate lastPaidDate, boolean hasHealthChanged) {
        this.reinstatementReason = reinstatementReason;
        this.unpaidPremium = unpaidPremium;
        this.appliedAt = appliedAt;
        this.desiredDate = desiredDate;
        this.lastPaidDate = lastPaidDate;
        this.hasHealthChanged = hasHealthChanged;
    }

    public void applyReinstatement()       {}
    public void calculateUnpaidPremium()   {}
    public void processReinstatement()     {}

    public String              getReinstatementId()                    { return reinstatementId; }
    public void                setReinstatementId(String v)           { this.reinstatementId = v; }
    public String              getPolicyNumber()                       { return policyNumber; }
    public void                setPolicyNumber(String v)              { this.policyNumber = v; }
    public LocalDateTime       getAppliedAt()                         { return appliedAt; }
    public void                setAppliedAt(LocalDateTime v)          { this.appliedAt = v; }
    public LocalDateTime       getDesiredDate()                       { return desiredDate; }
    public void                setDesiredDate(LocalDateTime v)        { this.desiredDate = v; }
    public boolean             isHasHealthChanged()                   { return hasHealthChanged; }
    public void                setHasHealthChanged(boolean v)         { this.hasHealthChanged = v; }
    public LocalDate           getLastPaidDate()                      { return lastPaidDate; }
    public void                setLastPaidDate(LocalDate v)           { this.lastPaidDate = v; }
    public LocalDateTime       getProcessedAt()                       { return processedAt; }
    public void                setProcessedAt(LocalDateTime v)        { this.processedAt = v; }
    public ReinstatementReason   getReinstatementReason()                      { return reinstatementReason; }
    public void                  setReinstatementReason(ReinstatementReason v) { this.reinstatementReason = v; }
    public String                getReinstatementStatus()                      { return reinstatementStatus; }
    public void                  setReinstatementStatus(String v)              { this.reinstatementStatus = v; }
    public BigDecimal            getUnpaidPremium()                            { return unpaidPremium; }
    public void                  setUnpaidPremium(BigDecimal v)                { this.unpaidPremium = v; }
    public UnderwritingResultType getUnderwritingResult()                      { return underwritingResult; }
    public void                  setUnderwritingResult(UnderwritingResultType v){ this.underwritingResult = v; }

    @Override
    public String toString() {
        return "Reinstatement{reinstatementReason=" + reinstatementReason + ", unpaidPremium=" + unpaidPremium + "}";
    }
}
