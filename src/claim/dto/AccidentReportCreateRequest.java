package claim.dto;

import enums.AccidentDetailsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class AccidentReportCreateRequest {

    @NotBlank
    @Size(max = 50)
    private String policyNumber;

    @NotNull
    private LocalDateTime accidentAt;

    @NotBlank
    private String accidentDescription;

    @NotBlank
    private String damageDetails;

    private AccidentDetailsType accidentType;

    @Size(max = 255)
    private String accidentReportDocumentName;

    @Size(max = 255)
    private String medicalCertificateFileName;

    @Size(max = 255)
    private String claimDocumentName;

    private boolean submitDocumentsLater;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public LocalDateTime getAccidentAt() {
        return accidentAt;
    }

    public void setAccidentAt(LocalDateTime accidentAt) {
        this.accidentAt = accidentAt;
    }

    public String getAccidentDescription() {
        return accidentDescription;
    }

    public void setAccidentDescription(String accidentDescription) {
        this.accidentDescription = accidentDescription;
    }

    public String getDamageDetails() {
        return damageDetails;
    }

    public void setDamageDetails(String damageDetails) {
        this.damageDetails = damageDetails;
    }

    public AccidentDetailsType getAccidentType() {
        return accidentType;
    }

    public void setAccidentType(AccidentDetailsType accidentType) {
        this.accidentType = accidentType;
    }

    public String getAccidentReportDocumentName() {
        return accidentReportDocumentName;
    }

    public void setAccidentReportDocumentName(String accidentReportDocumentName) {
        this.accidentReportDocumentName = accidentReportDocumentName;
    }

    public String getMedicalCertificateFileName() {
        return medicalCertificateFileName;
    }

    public void setMedicalCertificateFileName(String medicalCertificateFileName) {
        this.medicalCertificateFileName = medicalCertificateFileName;
    }

    public String getClaimDocumentName() {
        return claimDocumentName;
    }

    public void setClaimDocumentName(String claimDocumentName) {
        this.claimDocumentName = claimDocumentName;
    }

    public boolean isSubmitDocumentsLater() { return submitDocumentsLater; }

    public void setSubmitDocumentsLater(boolean submitDocumentsLater) {
        this.submitDocumentsLater = submitDocumentsLater;
    }
}
