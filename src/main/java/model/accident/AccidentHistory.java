package model.accident;

import enums.AccidentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 사고 이력 도메인 모델 — 피보험자의 과거 사고 및 청구 이력 관리
public class AccidentHistory {

    private String historyId;
    private AccidentType accidentType;
    private BigDecimal claimedAmount;
    private String diagnosisCode;
    private String diagnosisName;
    private boolean hasSurgery;
    private LocalDateTime hospitalizationPeriod;
    private String location;
    private LocalDateTime occurredAt;
    private LocalDateTime paidAt;
    private String receiptNumber;
    private LocalDateTime receivedAt;
    private BigDecimal recognizedAmount;
    private String treatmentDetails;

    public AccidentHistory() {}

    public AccidentHistory(String receiptNumber, AccidentType accidentType, String location,
                           LocalDateTime occurredAt, LocalDateTime receivedAt,
                           BigDecimal claimedAmount, BigDecimal recognizedAmount,
                           String diagnosisCode, String diagnosisName, String treatmentDetails,
                           LocalDateTime hospitalizationPeriod, boolean hasSurgery,
                           LocalDateTime paidAt) {
        this.receiptNumber = receiptNumber;
        this.accidentType = accidentType;
        this.location = location;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
        this.claimedAmount = claimedAmount;
        this.recognizedAmount = recognizedAmount;
        this.diagnosisCode = diagnosisCode;
        this.diagnosisName = diagnosisName;
        this.treatmentDetails = treatmentDetails;
        this.hospitalizationPeriod = hospitalizationPeriod;
        this.hasSurgery = hasSurgery;
        this.paidAt = paidAt;
    }

    // 심사에 사고이력 반영
    public void applyToUnderwriting() {}

    // 사고 이력 목록 조회
    public List<AccidentHistory> getAccidentHistory() { return new ArrayList<>(); }

    public String         getHistoryId()                        { return historyId; }
    public void           setHistoryId(String v)                { this.historyId = v; }
    public AccidentType   getAccidentType()                     { return accidentType; }
    public void           setAccidentType(AccidentType v)       { this.accidentType = v; }
    public BigDecimal     getClaimedAmount()                    { return claimedAmount; }
    public void           setClaimedAmount(BigDecimal v)        { this.claimedAmount = v; }
    public String         getDiagnosisCode()                    { return diagnosisCode; }
    public void           setDiagnosisCode(String v)            { this.diagnosisCode = v; }
    public String         getDiagnosisName()                    { return diagnosisName; }
    public void           setDiagnosisName(String v)            { this.diagnosisName = v; }
    public boolean        isHasSurgery()                        { return hasSurgery; }
    public void           setHasSurgery(boolean v)              { this.hasSurgery = v; }
    public LocalDateTime  getHospitalizationPeriod()            { return hospitalizationPeriod; }
    public void           setHospitalizationPeriod(LocalDateTime v){ this.hospitalizationPeriod = v; }
    public String         getLocation()                         { return location; }
    public void           setLocation(String v)                 { this.location = v; }
    public LocalDateTime  getOccurredAt()                       { return occurredAt; }
    public void           setOccurredAt(LocalDateTime v)        { this.occurredAt = v; }
    public LocalDateTime  getPaidAt()                           { return paidAt; }
    public void           setPaidAt(LocalDateTime v)            { this.paidAt = v; }
    public String         getReceiptNumber()                    { return receiptNumber; }
    public void           setReceiptNumber(String v)            { this.receiptNumber = v; }
    public LocalDateTime  getReceivedAt()                       { return receivedAt; }
    public void           setReceivedAt(LocalDateTime v)        { this.receivedAt = v; }
    public BigDecimal     getRecognizedAmount()                 { return recognizedAmount; }
    public void           setRecognizedAmount(BigDecimal v)     { this.recognizedAmount = v; }
    public String         getTreatmentDetails()                 { return treatmentDetails; }
    public void           setTreatmentDetails(String v)         { this.treatmentDetails = v; }

    @Override
    public String toString() {
        return "AccidentHistory{receiptNumber='" + receiptNumber + "', accidentType=" + accidentType + ", occurredAt=" + occurredAt + "}";
    }
}
