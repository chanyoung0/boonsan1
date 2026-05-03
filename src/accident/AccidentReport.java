package accident;

import enums.AccidentDetailsType;

import java.time.LocalDateTime;

public class AccidentReport {

    private String accidentDescription;
    private AccidentDetailsType accidentDetails;
    private LocalDateTime createdAt;
    private String damageDetails;
    private String reportNo;

    public void changeStatus() {}

    public void deferSubmission() {}

    public void retrieveContactInfo() {}

    public void saveAccidentDocumentPending() {}
}
