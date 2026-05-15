package model.accident;

import enums.AccidentDetailsType;
import model.document.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 사고 접수 도메인 모델 — 보험사고 신고 및 접수 정보 관리
public class AccidentReport {

    private String reportNo;
    private AccidentDetailsType accidentStatus;
    private LocalDateTime createdAt;
    private String accidentDescription;
    private String damageDetails;
    private final List<Document> documents = new ArrayList<>();
    private DamageInvestigation damageInvestigation;

    public AccidentReport() {}

    // 사고 접수 기본 정보로 초기화
    public AccidentReport(String reportNo, AccidentDetailsType accidentStatus,
                          String accidentDescription, String damageDetails) {
        this.reportNo = reportNo;
        this.accidentStatus = accidentStatus;
        this.accidentDescription = accidentDescription;
        this.damageDetails = damageDetails;
        this.createdAt = LocalDateTime.now();
    }

    // 사고 접수 상태 변경
    public void changeStatus() {}

    // 사고 접수 연기 — 연기 처리 가능 여부 반환
    public boolean deferSubmission() {
        return true;
    }

    // 사고 접수 등록
    public void register() {}

    // 계약 정보 조회
    public void retrieveContractInfo() {}

    // 서류 미비 상태로 저장 — 저장 성공 여부 반환
    public boolean saveAsDocumentPending() {
        return true;
    }

    public String getReportNo() { return reportNo; }
    public AccidentDetailsType getAccidentStatus() { return accidentStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getAccidentDescription() { return accidentDescription; }
    public String getDamageDetails() { return damageDetails; }
    public List<Document> getDocuments() { return documents; }
    public DamageInvestigation getDamageInvestigation() { return damageInvestigation; }

    public void setReportNo(String s) { this.reportNo = s; }
    public void setAccidentStatus(AccidentDetailsType t) { this.accidentStatus = t; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public void setAccidentDescription(String s) { this.accidentDescription = s; }
    public void setDamageDetails(String s) { this.damageDetails = s; }
    public void addDocument(Document d) { this.documents.add(d); }
    public void setDamageInvestigation(DamageInvestigation di) { this.damageInvestigation = di; }

    @Override
    public String toString() {
        return "AccidentReport{reportNo='" + reportNo + "', status=" + accidentStatus
                + ", createdAt=" + createdAt + "}";
    }
}
