package model.underwriting;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 공동보험 도메인 모델 — 위험 분담을 위한 공동인수 처리 정보 관리
public class Coinsurance {

    private LocalDateTime receivedAt;
    private BigDecimal retainedAmount;
    private float retainedShareRate;
    private final List<Coinsurer> coinsurers = new ArrayList<>();

    public Coinsurance() {}

    // 공동보험 기본 정보로 초기화
    public Coinsurance(LocalDateTime receivedAt, BigDecimal retainedAmount, float retainedShareRate) {
        this.receivedAt = receivedAt;
        this.retainedAmount = retainedAmount;
        this.retainedShareRate = retainedShareRate;
    }

    // 보험료 할당
    public void allocatePremium() {}

    // 공동인수 신청
    public void applyForCoinsurance() {}

    // 보유액 계산
    public void calculateRetainedAmount() {}

    // 공동인수사 승인 확인
    public void checkApproval() {}

    // 출재 처리
    public void processCession() {}

    // 참여 요청 전송
    public void sendParticipationRequest() {}

    public LocalDateTime getReceivedAt() { return receivedAt; }
    public BigDecimal getRetainedAmount() { return retainedAmount; }
    public float getRetainedShareRate() { return retainedShareRate; }
    public List<Coinsurer> getCoinsurers() { return coinsurers; }

    public void addCoinsurer(Coinsurer c) { this.coinsurers.add(c); }
    public void setReceivedAt(LocalDateTime t) { this.receivedAt = t; }
    public void setRetainedAmount(BigDecimal v) { this.retainedAmount = v; }
    public void setRetainedShareRate(float v) { this.retainedShareRate = v; }

    @Override
    public String toString() {
        return "Coinsurance{retained=" + retainedAmount + ", shareRate=" + retainedShareRate
                + "%, coinsurers=" + coinsurers.size() + "}";
    }
}
