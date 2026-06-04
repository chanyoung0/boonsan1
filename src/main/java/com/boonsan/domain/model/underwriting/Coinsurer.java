package com.boonsan.domain.model.underwriting;

import com.boonsan.domain.enums.ApprovalStatus;

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

    // 참여 결과 조회
    public ApprovalStatus getResult() { return isApproved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED; }

    // 참여 결과 등록
    public void registerResult() {}
}
