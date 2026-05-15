package model.accident;

import enums.AcceptanceStatus;

import java.math.BigDecimal;

// 이의 신청 도메인 모델 — 보험금 지급 결과에 대한 이의 신청 정보 관리
public class Objection {

    private String objectionId;
    private String claimantInfo;
    private String objectionReason;
    private String originalPaymentDetails;
    private BigDecimal adjustedAmount;
    private AcceptanceStatus acceptanceStatus;
    private String transferReason;

    public Objection() {}

    // 이의 신청 기본 정보로 초기화
    public Objection(String objectionId, String claimantInfo, String objectionReason, String originalPaymentDetails) {
        this.objectionId = objectionId;
        this.claimantInfo = claimantInfo;
        this.objectionReason = objectionReason;
        this.originalPaymentDetails = originalPaymentDetails;
        this.acceptanceStatus = AcceptanceStatus.PENDING;
    }

    // 이의 수용 — 수용 상태 반환
    public AcceptanceStatus acceptObjection() {
        this.acceptanceStatus = AcceptanceStatus.ACCEPTED;
        return this.acceptanceStatus;
    }

    // 이의 기각 — 거절 상태 반환
    public AcceptanceStatus rejectObjection() {
        this.acceptanceStatus = AcceptanceStatus.REJECTED;
        return this.acceptanceStatus;
    }

    // 법률과 이관
    public void transferToLegal() {}

    // 법률 검토 결과 수신
    public void receiveLegalResult() {}

    public String getObjectionId() { return objectionId; }
    public String getClaimantInfo() { return claimantInfo; }
    public String getObjectionReason() { return objectionReason; }
    public String getOriginalPaymentDetails() { return originalPaymentDetails; }
    public BigDecimal getAdjustedAmount() { return adjustedAmount; }
    public AcceptanceStatus getAcceptanceStatus() { return acceptanceStatus; }
    public String getTransferReason() { return transferReason; }

    public void setObjectionId(String s) { this.objectionId = s; }
    public void setClaimantInfo(String s) { this.claimantInfo = s; }
    public void setObjectionReason(String s) { this.objectionReason = s; }
    public void setOriginalPaymentDetails(String s) { this.originalPaymentDetails = s; }
    public void setAdjustedAmount(BigDecimal v) { this.adjustedAmount = v; }
    public void setAcceptanceStatus(AcceptanceStatus s) { this.acceptanceStatus = s; }
    public void setTransferReason(String s) { this.transferReason = s; }

    @Override
    public String toString() {
        return "Objection{id='" + objectionId + "', status=" + acceptanceStatus
                + ", adjusted=" + adjustedAmount + "}";
    }
}
