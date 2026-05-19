package model.contract;

import enums.ReinstatementReason;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 부활 도메인 모델 — 실효 계약 부활 신청 및 처리 정보 관리
public class Reinstatement {

    private LocalDateTime appliedAt;
    private LocalDateTime desiredDate;
    private boolean hasHealthChanged;
    private LocalDate lastPaidDate;
    private LocalDateTime processedAt;
    private ReinstatementReason reinstatementReason;
    private BigDecimal unpaidPremium;

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

    // 부활 신청
    public void applyReinstatement() {
        if (reinstatementReason == null)
            throw new IllegalStateException("부활 사유가 설정되지 않았습니다.");
        if (this.appliedAt == null) this.appliedAt = LocalDateTime.now();
    }

    // 미납 보험료 계산
    public void calculateUnpaidPremium() {
        if (lastPaidDate == null)
            throw new IllegalStateException("최종 납입일이 설정되지 않았습니다.");
        if (unpaidPremium == null) this.unpaidPremium = BigDecimal.ZERO;
    }

    // 부활 처리
    public void processReinstatement() {
        if (unpaidPremium == null)
            throw new IllegalStateException("미납 보험료가 계산되지 않았습니다.");
        this.processedAt = LocalDateTime.now();
    }

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
    public ReinstatementReason getReinstatementReason()               { return reinstatementReason; }
    public void                setReinstatementReason(ReinstatementReason v){ this.reinstatementReason = v; }
    public BigDecimal          getUnpaidPremium()                     { return unpaidPremium; }
    public void                setUnpaidPremium(BigDecimal v)         { this.unpaidPremium = v; }

    @Override
    public String toString() {
        return "Reinstatement{reinstatementReason=" + reinstatementReason + ", unpaidPremium=" + unpaidPremium + "}";
    }
}
