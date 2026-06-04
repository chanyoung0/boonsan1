package com.boonsan.claim.dto;

import com.boonsan.enums.AccidentDetailsType;
import com.boonsan.enums.AccidentReportStatus;
import com.boonsan.model.accident.AccidentReport;

import java.time.LocalDateTime;

public class AccidentReportResponse {

    private final String accidentNumber;
    private final String policyNumber;
    private final LocalDateTime accidentAt;
    private final String accidentDescription;
    private final String damageDetails;
    private final AccidentDetailsType accidentType;
    private final AccidentReportStatus accidentStatus;
    private final String accidentReportDocumentName;
    private final String medicalCertificateFileName;
    private final String claimDocumentName;
    private final LocalDateTime createdAt;

    private AccidentReportResponse(
            String accidentNumber,
            String policyNumber,
            LocalDateTime accidentAt,
            String accidentDescription,
            String damageDetails,
            AccidentDetailsType accidentType,
            AccidentReportStatus accidentStatus,
            String accidentReportDocumentName,
            String medicalCertificateFileName,
            String claimDocumentName,
            LocalDateTime createdAt
    ) {
        this.accidentNumber = accidentNumber;
        this.policyNumber = policyNumber;
        this.accidentAt = accidentAt;
        this.accidentDescription = accidentDescription;
        this.damageDetails = damageDetails;
        this.accidentType = accidentType;
        this.accidentStatus = accidentStatus;
        this.accidentReportDocumentName = accidentReportDocumentName;
        this.medicalCertificateFileName = medicalCertificateFileName;
        this.claimDocumentName = claimDocumentName;
        this.createdAt = createdAt;
    }

    public static AccidentReportResponse from(AccidentReport report) {
        return new AccidentReportResponse(
                report.getReportNo(),
                report.getPolicyNumber(),
                report.getAccidentAt(),
                report.getAccidentDescription(),
                report.getDamageDetails(),
                report.getAccidentType(),
                report.getAccidentStatus(),
                report.getAccidentReportDocumentName(),
                report.getMedicalCertificateFileName(),
                report.getClaimDocumentName(),
                report.getCreatedAt()
        );
    }

    public String getAccidentNumber() { return accidentNumber; }

    public String getPolicyNumber() { return policyNumber; }

    public LocalDateTime getAccidentAt() { return accidentAt; }

    public String getAccidentDescription() { return accidentDescription; }

    public String getDamageDetails() { return damageDetails; }

    public AccidentDetailsType getAccidentType() { return accidentType; }

    public AccidentReportStatus getAccidentStatus() { return accidentStatus; }

    public String getAccidentReportDocumentName() { return accidentReportDocumentName; }

    public String getMedicalCertificateFileName() { return medicalCertificateFileName; }

    public String getClaimDocumentName() { return claimDocumentName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
