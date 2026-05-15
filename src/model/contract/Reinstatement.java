package model.contract;

import enums.ReinstatementReason;
import model.underwriting.UnderwritingRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 부활 도메인 모델 — 실효 계약 부활 신청 및 처리 정보 관리
public class Reinstatement {

    private LocalDateTime appliedAt;
    private LocalDateTime desiredDate;
    private boolean hasHealthChanged;
    private LocalDate lastPaidDate;
    private LocalDateTime processedAt;
    private ReinstatementReason reinstatementReason;
    private BigDecimal unpaidPremium;
    private final List<UnderwritingRequest> underwritingRequests = new ArrayList<>();

    public Reinstatement() {}

    // 부활 신청 기본 정보로 초기화
    public Reinstatement(ReinstatementReason reason, BigDecimal unpaidPremium, LocalDateTime desiredDate, LocalDate lastPaidDate) {
        this.reinstatementReason = reason;
        this.unpaidPremium = unpaidPremium;
        this.desiredDate = desiredDate;
        this.lastPaidDate = lastPaidDate;
        this.appliedAt = LocalDateTime.now();
    }

    // 부활 신청
    public void applyReinstatement() {}

    // 미납 보험료 계산
    public void calculateUnpaidPremium() {}

    // 부활 처리
    public void processReinstatement() {}

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public LocalDateTime getDesiredDate() { return desiredDate; }
    public boolean isHasHealthChanged() { return hasHealthChanged; }
    public LocalDate getLastPaidDate() { return lastPaidDate; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public ReinstatementReason getReinstatementReason() { return reinstatementReason; }
    public BigDecimal getUnpaidPremium() { return unpaidPremium; }
    public List<UnderwritingRequest> getUnderwritingRequests() { return underwritingRequests; }

    public void setAppliedAt(LocalDateTime t) { this.appliedAt = t; }
    public void setDesiredDate(LocalDateTime t) { this.desiredDate = t; }
    public void setHasHealthChanged(boolean b) { this.hasHealthChanged = b; }
    public void setLastPaidDate(LocalDate d) { this.lastPaidDate = d; }
    public void setProcessedAt(LocalDateTime t) { this.processedAt = t; }
    public void setReinstatementReason(ReinstatementReason r) { this.reinstatementReason = r; }
    public void setUnpaidPremium(BigDecimal v) { this.unpaidPremium = v; }
    public void addUnderwritingRequest(UnderwritingRequest r) { this.underwritingRequests.add(r); }

    @Override
    public String toString() {
        return "Reinstatement{reason=" + reinstatementReason + ", unpaid=" + unpaidPremium
                + ", appliedAt=" + appliedAt + "}";
    }
}
