package model.accident;

import enums.AccidentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 사고 이력 도메인 모델 — 피보험자의 과거 사고 및 청구 이력 관리
public class AccidentHistory {

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

    public void applyToUnderwriting() {}

    public void getAccidentHistory() {}
}
