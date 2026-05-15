package model.accident;

import enums.AccidentDetailsType;

import java.time.LocalDateTime;

// 사고 접수 도메인 모델 — 보험사고 신고 및 접수 정보 관리
public class AccidentReport {

    private String accidentDescription;
    private AccidentDetailsType accidentStatus;
    private LocalDateTime createdAt;
    private String damageDetails;
    private String reportNo;

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

    public String getReportNo() { return reportNo; }
}
