package model.accident;

import model.document.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 사고 접수 도메인 모델 — 보험사고 신고 및 접수 정보 관리
public class AccidentReport {

    private String accidentDescription;
    private String accidentStatus;
    private LocalDateTime createdAt;
    private String damageDetails;
    private String reportNo;

    private List<Document> documentList;
    private DamageInvestigation damageInvestigation;

    public AccidentReport() {
        this.documentList = new ArrayList<>();
    }

    public AccidentReport(String reportNo, String accidentDescription, String damageDetails,
                          String accidentStatus, LocalDateTime createdAt) {
        this.reportNo = reportNo;
        this.accidentDescription = accidentDescription;
        this.damageDetails = damageDetails;
        this.accidentStatus = accidentStatus;
        this.createdAt = createdAt;
        this.documentList = new ArrayList<>();
    }

    // 사고 접수 상태 변경
    public void changeStatus() {}

    // 사고 접수 연기 — 성공 여부 반환
    public boolean deferSubmission() { return true; }

    // 사고 접수 등록
    public void register() {}

    // 계약 정보 조회
    public void retrieveContractInfo() {}

    // 서류 미비 상태로 저장 — 성공 여부 반환
    public boolean saveAsDocumentPending() { return true; }

    public String              getReportNo()                         { return reportNo; }
    public void                setReportNo(String v)                 { this.reportNo = v; }
    public String              getAccidentDescription()              { return accidentDescription; }
    public void                setAccidentDescription(String v)      { this.accidentDescription = v; }
    public String              getAccidentStatus()                   { return accidentStatus; }
    public void                setAccidentStatus(String v)           { this.accidentStatus = v; }
    public LocalDateTime       getCreatedAt()                        { return createdAt; }
    public void                setCreatedAt(LocalDateTime v)         { this.createdAt = v; }
    public String              getDamageDetails()                    { return damageDetails; }
    public void                setDamageDetails(String v)            { this.damageDetails = v; }
    public List<Document>      getDocumentList()                     { return documentList; }
    public void                setDocumentList(List<Document> v)     { this.documentList = v; }
    public DamageInvestigation getDamageInvestigation()              { return damageInvestigation; }
    public void                setDamageInvestigation(DamageInvestigation v){ this.damageInvestigation = v; }

    @Override
    public String toString() {
        return "AccidentReport{reportNo='" + reportNo + "', accidentStatus=" + accidentStatus + "}";
    }
}
