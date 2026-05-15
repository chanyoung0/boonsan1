package model.accident;

import enums.AccidentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 사고 이력 도메인 모델 — 피보험자의 과거 사고 및 청구 이력 관리
public class AccidentHistory {

    private String receiptNumber;
    private AccidentType accidentType;
    private LocalDateTime occurredAt;
    private LocalDateTime receivedAt;
    private LocalDateTime paidAt;
    private LocalDateTime hospitalizationPeriod;
    private String location;
    private String diagnosisCode;
    private String diagnosisName;
    private boolean hasSurgery;
    private String treatmentDetails;
    private BigDecimal claimedAmount;
    private BigDecimal recognizedAmount;
    private final List<AccidentHistory> accidentHistories = new ArrayList<>();

    public AccidentHistory() {}

    // 사고 이력 기본 정보로 초기화
    public AccidentHistory(String receiptNumber, AccidentType accidentType,
                           LocalDateTime occurredAt, BigDecimal claimedAmount, BigDecimal recognizedAmount) {
        this.receiptNumber = receiptNumber;
        this.accidentType = accidentType;
        this.occurredAt = occurredAt;
        this.claimedAmount = claimedAmount;
        this.recognizedAmount = recognizedAmount;
    }

    // 심사에 사고 이력 반영
    public void applyToUnderwriting() {}

    // 사고 이력 목록 조회
    public List<AccidentHistory> getAccidentHistory() {
        return accidentHistories;
    }

    public void addAccidentHistory(AccidentHistory h) { this.accidentHistories.add(h); }

    public String getReceiptNumber() { return receiptNumber; }
    public AccidentType getAccidentType() { return accidentType; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getHospitalizationPeriod() { return hospitalizationPeriod; }
    public String getLocation() { return location; }
    public String getDiagnosisCode() { return diagnosisCode; }
    public String getDiagnosisName() { return diagnosisName; }
    public boolean isHasSurgery() { return hasSurgery; }
    public String getTreatmentDetails() { return treatmentDetails; }
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public BigDecimal getRecognizedAmount() { return recognizedAmount; }

    public void setReceiptNumber(String s) { this.receiptNumber = s; }
    public void setAccidentType(AccidentType t) { this.accidentType = t; }
    public void setOccurredAt(LocalDateTime t) { this.occurredAt = t; }
    public void setReceivedAt(LocalDateTime t) { this.receivedAt = t; }
    public void setPaidAt(LocalDateTime t) { this.paidAt = t; }
    public void setHospitalizationPeriod(LocalDateTime t) { this.hospitalizationPeriod = t; }
    public void setLocation(String s) { this.location = s; }
    public void setDiagnosisCode(String s) { this.diagnosisCode = s; }
    public void setDiagnosisName(String s) { this.diagnosisName = s; }
    public void setHasSurgery(boolean b) { this.hasSurgery = b; }
    public void setTreatmentDetails(String s) { this.treatmentDetails = s; }
    public void setClaimedAmount(BigDecimal v) { this.claimedAmount = v; }
    public void setRecognizedAmount(BigDecimal v) { this.recognizedAmount = v; }

    @Override
    public String toString() {
        return "AccidentHistory{receipt='" + receiptNumber + "', type=" + accidentType
                + ", occurredAt=" + occurredAt + "}";
    }
}
