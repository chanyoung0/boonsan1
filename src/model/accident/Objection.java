package model.accident;

import enums.AcceptanceStatus;

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

    public Objection() {}

    public Objection(String objectionId, String claimantInfo, String objectionReason,
                     String originalPaymentDetails, AcceptanceStatus acceptanceStatus) {
        this.objectionId = objectionId;
        this.claimantInfo = claimantInfo;
        this.objectionReason = objectionReason;
        this.originalPaymentDetails = originalPaymentDetails;
        this.acceptanceStatus = acceptanceStatus;
    }

    // 이의신청 수용 — 수용 결과 상태 반환
    public AcceptanceStatus acceptObjection() {
        this.acceptanceStatus = AcceptanceStatus.ACCEPTED;
        return acceptanceStatus;
    }

    // 법률과 결과 수신
    public void receiveLegalResult() {}

    // 이의신청 기각 — 기각 결과 상태 반환
    public AcceptanceStatus rejectObjection() {
        this.acceptanceStatus = AcceptanceStatus.REJECTED;
        return acceptanceStatus;
    }

    // 법률과 이관
    public void transferToLegal() {}

    public AcceptanceStatus getAcceptanceStatus()                   { return acceptanceStatus; }
    public void             setAcceptanceStatus(AcceptanceStatus v) { this.acceptanceStatus = v; }
    public BigDecimal       getAdjustedAmount()                     { return adjustedAmount; }
    public void             setAdjustedAmount(BigDecimal v)         { this.adjustedAmount = v; }
    public String           getClaimantInfo()                       { return claimantInfo; }
    public void             setClaimantInfo(String v)               { this.claimantInfo = v; }
    public String           getObjectionId()                        { return objectionId; }
    public void             setObjectionId(String v)                { this.objectionId = v; }
    public String           getObjectionReason()                    { return objectionReason; }
    public void             setObjectionReason(String v)            { this.objectionReason = v; }
    public String           getOriginalPaymentDetails()             { return originalPaymentDetails; }
    public void             setOriginalPaymentDetails(String v)     { this.originalPaymentDetails = v; }
    public String           getTransferReason()                     { return transferReason; }
    public void             setTransferReason(String v)             { this.transferReason = v; }

    @Override
    public String toString() {
        return "Objection{objectionId='" + objectionId + "', acceptanceStatus=" + acceptanceStatus + "}";
    }
}
