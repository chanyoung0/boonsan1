package model.underwriting;

import enums.ApprovalStatus;

import java.math.BigDecimal;

// 공동보험사 도메인 모델 — 공동인수 계약에 참여하는 보험사 정보 관리
public class Coinsurer {

    private BigDecimal allocatedPremium;
    private String companyName;
    private boolean isApproved;
    private float maxAcceptableShareRate;
    private String rejectionReason;
    private BigDecimal retainedAmount;
    private float shareRate;

    public Coinsurer() {}

    public Coinsurer(String companyName, float shareRate, float maxAcceptableShareRate,
                     BigDecimal allocatedPremium, BigDecimal retainedAmount) {
        this.companyName = companyName;
        this.shareRate = shareRate;
        this.maxAcceptableShareRate = maxAcceptableShareRate;
        this.allocatedPremium = allocatedPremium;
        this.retainedAmount = retainedAmount;
    }

    // 참여 결과 조회
    public ApprovalStatus getResult() {
        return isApproved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
    }

    // 참여 결과 등록
    public void registerResult() {
        if (companyName == null || companyName.isEmpty())
            throw new IllegalStateException("공동인수사명이 없습니다.");
        if (rejectionReason != null && !rejectionReason.isEmpty())
            this.isApproved = false;
    }

    public BigDecimal getAllocatedPremium()              { return allocatedPremium; }
    public void       setAllocatedPremium(BigDecimal v) { this.allocatedPremium = v; }
    public String     getCompanyName()                  { return companyName; }
    public void       setCompanyName(String v)          { this.companyName = v; }
    public boolean    isApproved()                      { return isApproved; }
    public void       setApproved(boolean v)            { this.isApproved = v; }
    public float      getMaxAcceptableShareRate()       { return maxAcceptableShareRate; }
    public void       setMaxAcceptableShareRate(float v){ this.maxAcceptableShareRate = v; }
    public String     getRejectionReason()              { return rejectionReason; }
    public void       setRejectionReason(String v)      { this.rejectionReason = v; }
    public BigDecimal getRetainedAmount()               { return retainedAmount; }
    public void       setRetainedAmount(BigDecimal v)   { this.retainedAmount = v; }
    public float      getShareRate()                    { return shareRate; }
    public void       setShareRate(float v)             { this.shareRate = v; }

    @Override
    public String toString() {
        return "Coinsurer{companyName='" + companyName + "', shareRate=" + shareRate + ", isApproved=" + isApproved + "}";
    }
}
