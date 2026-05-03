package accident;

import enums.AccidentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccidentHistory {

    private AccidentType accidentType;
    private BigDecimal claimAmount;
    private String diagnosisCode;
    private boolean diagnosisNote;
    private String familyHistory;
    private LocalDateTime hospitalizationRecord;
    private String location;
    private LocalDateTime occurredAt;
    private LocalDateTime receiptAt;
    private BigDecimal recognizedAmount;
    private String treatmentDetails;

    public void applyToUnderwriting() {}

    public AccidentHistory getAccidentHistory() {
        return null;
    }
}
