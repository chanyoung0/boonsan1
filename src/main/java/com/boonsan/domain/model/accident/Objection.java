package com.boonsan.domain.model.accident;

import com.boonsan.domain.enums.AcceptanceStatus;

import java.math.BigDecimal;

// 이의 신청 도메인 모델 — 보험금 지급 결과에 대한 이의 신청 정보 관리
public class Objection {

    private AcceptanceStatus acceptanceStatus;
    private BigDecimal adjustedAmount;
    private String claimantInfo;
    private String objectionId;
    private String objectionReason;
    private String originalPaymentDetails;
    private String transferReason;

    public void acceptObjection() {}

    public void receiveLegalResult() {}

    public void rejectObjection() {}

    public void transferToLegal() {}
}
