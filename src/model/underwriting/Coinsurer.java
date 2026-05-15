package model.underwriting;

import enums.ApprovalStatus;
import enums.RejectionReason;

import java.math.BigDecimal;

// 공동보험사 도메인 모델 — 공동인수 계약 참여 보험사 정보 관리
public class Coinsurer {

    private String companyName;
    private float shareRate;
    private float maxAcceptableShareRate;
    private BigDecimal allocatedPremium;
    private BigDecimal retainedAmount;
    private boolean isApproved;
    private RejectionReason rejectionReason;

    public Coinsurer() {}

    // 공동보험사 기본 정보로 초기화
    public Coinsurer(String companyName, float shareRate, float maxAcceptableShareRate) {
        this.companyName = companyName;
        this.shareRate = shareRate;
        this.maxAcceptableShareRate = maxAcceptableShareRate;
        this.isApproved = false;
    }

    // 참여 결과 조회 — 승인/거절 상태 반환
    public ApprovalStatus getResult() {
        return isApproved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
    }

    // 참여 결과 등록
    public void registerResult() {}

    public String getCompanyName() { return companyName; }
    public float getShareRate() { return shareRate; }
    public float getMaxAcceptableShareRate() { return maxAcceptableShareRate; }
    public BigDecimal getAllocatedPremium() { return allocatedPremium; }
    public BigDecimal getRetainedAmount() { return retainedAmount; }
    public boolean isApproved() { return isApproved; }
    public RejectionReason getRejectionReason() { return rejectionReason; }

    public void setCompanyName(String s) { this.companyName = s; }
    public void setShareRate(float v) { this.shareRate = v; }
    public void setMaxAcceptableShareRate(float v) { this.maxAcceptableShareRate = v; }
    public void setAllocatedPremium(BigDecimal v) { this.allocatedPremium = v; }
    public void setRetainedAmount(BigDecimal v) { this.retainedAmount = v; }
    public void setApproved(boolean b) { this.isApproved = b; }
    public void setRejectionReason(RejectionReason r) { this.rejectionReason = r; }

    @Override
    public String toString() {
        return "Coinsurer{company='" + companyName + "', share=" + shareRate + "%, approved=" + isApproved + "}";
    }
}
