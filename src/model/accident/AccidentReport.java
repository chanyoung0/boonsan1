package model.accident;

import enums.AccidentDetailsType;
import enums.AccidentReportStatus;

import java.time.LocalDateTime;

// 사고 접수 도메인 모델 — 보험사고 신고 및 접수 정보 관리
public class AccidentReport {

    private String accidentDescription;
    private LocalDateTime accidentAt;
    private String accidentReportDocumentName;
    private AccidentReportStatus accidentStatus;
    private AccidentDetailsType accidentType;
    private String claimDocumentName;
    private LocalDateTime createdAt;
    private String damageDetails;
    private LocalDateTime documentSubmissionDeadline;
    private String medicalCertificateFileName;
    private String policyNumber;
    private String reportNo;

    public AccidentReport() {
    }

    public AccidentReport(
            String reportNo,
            String policyNumber,
            LocalDateTime accidentAt,
            String accidentDescription,
            String damageDetails,
            AccidentDetailsType accidentType,
            AccidentReportStatus accidentStatus,
            String accidentReportDocumentName,
            String medicalCertificateFileName,
            String claimDocumentName,
            LocalDateTime documentSubmissionDeadline,
            LocalDateTime createdAt
    ) {
        this.reportNo = reportNo;
        this.policyNumber = policyNumber;
        this.accidentAt = accidentAt;
        this.accidentDescription = accidentDescription;
        this.damageDetails = damageDetails;
        this.accidentType = accidentType;
        this.accidentStatus = accidentStatus;
        this.accidentReportDocumentName = accidentReportDocumentName;
        this.medicalCertificateFileName = medicalCertificateFileName;
        this.claimDocumentName = claimDocumentName;
        this.documentSubmissionDeadline = documentSubmissionDeadline;
        this.createdAt = createdAt;
    }

    // 사고 접수 상태 변경
    public void changeStatus() {}

    // 사고 접수 연기
    public void deferSubmission() {}

    // 사고 접수 등록
    public void register() {}

    // 계약 정보 조회
    public void retrieveContractInfo() {}

    // 서류 미비 상태로 저장
    public void saveAsDocumentPending() {}

    public String getAccidentDescription() { return accidentDescription; }

    public void setAccidentDescription(String accidentDescription) {
        this.accidentDescription = accidentDescription;
    }

    public AccidentReportStatus getAccidentStatus() { return accidentStatus; }

    public void setAccidentStatus(AccidentReportStatus accidentStatus) {
        this.accidentStatus = accidentStatus;
    }

    public AccidentDetailsType getAccidentType() { return accidentType; }

    public void setAccidentType(AccidentDetailsType accidentType) {
        this.accidentType = accidentType;
    }

    public LocalDateTime getAccidentAt() { return accidentAt; }

    public void setAccidentAt(LocalDateTime accidentAt) {
        this.accidentAt = accidentAt;
    }

    public String getAccidentReportDocumentName() { return accidentReportDocumentName; }

    public void setAccidentReportDocumentName(String accidentReportDocumentName) {
        this.accidentReportDocumentName = accidentReportDocumentName;
    }

    public String getClaimDocumentName() { return claimDocumentName; }

    public void setClaimDocumentName(String claimDocumentName) {
        this.claimDocumentName = claimDocumentName;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDamageDetails() { return damageDetails; }

    public void setDamageDetails(String damageDetails) {
        this.damageDetails = damageDetails;
    }

    public LocalDateTime getDocumentSubmissionDeadline() { return documentSubmissionDeadline; }

    public void setDocumentSubmissionDeadline(LocalDateTime documentSubmissionDeadline) {
        this.documentSubmissionDeadline = documentSubmissionDeadline;
    }

    public String getMedicalCertificateFileName() { return medicalCertificateFileName; }

    public void setMedicalCertificateFileName(String medicalCertificateFileName) {
        this.medicalCertificateFileName = medicalCertificateFileName;
    }

    public String getPolicyNumber() { return policyNumber; }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getReportNo() { return reportNo; }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }
}
