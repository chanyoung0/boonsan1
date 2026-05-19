package console.accident;

import model.accident.AccidentReport;
import service.accident.AccidentReportService;

import static common.ConsoleUtil.*;

import java.time.LocalDateTime;

// 사고 접수 콘솔 I/O — 보험가입자/보험사 직원 유스케이스 입출력 전담
public class AccidentReportConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 사고를 접수한다");
        System.out.println("액터: 보험가입자, 보험사 직원");
        line();

        System.out.println("\n[보험가입자] '사고 접수' 버튼을 누릅니다.");
        System.out.println("[시스템] 사고 접수 화면:");
        String policyNumber = input("보험 증권번호");
        String accidentAtInput = input("사고 일시 (YYYY-MM-DD HH:MM)");
        String accidentDescription = input("사고 경위");
        String damageDetails = input("피해 내용 (예: 차량 파손, 부상)");

        System.out.println("[보험가입자] '접수' 버튼을 누릅니다.");
        enter();

        String contractStatus = input("계약상태를 입력하세요(유효/실효/해지/만기)");
        if (!"유효".equals(contractStatus)) {
            System.out.println("[시스템] 유효하지 않은 계약입니다. 사고 접수를 종료합니다. | 증권번호: " + policyNumber);
            return;
        }

        AccidentReport accidentReport = new AccidentReport();
        accidentReport.setAccidentDescription(accidentDescription);
        accidentReport.setDamageDetails(damageDetails);
        accidentReport.setCreatedAt(LocalDateTime.now());
        accidentReport.setPolicyNumber(policyNumber);
        accidentReport.setAccidentAt(AccidentReportService.parseAccidentAt(accidentAtInput));
        accidentReport.retrieveContractInfo();

        System.out.println("[시스템] 계약 정보:");
        System.out.println("  피보험자명: 홍길동 | 보험 종류: 자동차보험 | 보장 범위: 대인/대물/자손");

        System.out.println("\n서류 제출 방법: 1. 지금 업로드  2. 나중에 제출");
        System.out.print(">> 선택: ");
        String docChoice = sc.nextLine().trim();

        if (AccidentReportService.isDocumentDeferred(docChoice)) {
            accidentReport.deferSubmission();
            accidentReport.saveAsDocumentPending();
            System.out.println("[시스템] '서류 미제출' 상태로 접수 처리합니다.");
        } else {
            input("  사고경위서 파일명");
            input("  진단서 파일명 (없으면 Enter)");
            input("  청구서류 파일명");
            enter();
        }

        String reportNo = AccidentReportService.generateReportNo();
        accidentReport.setReportNo(reportNo);
        accidentReport.register();
        accidentReport.changeStatus();

        System.out.println("[시스템] 접수 내용을 DB에 저장 중...");
        if (!AccidentReportService.saveReport(accidentReport)) {
            System.out.println("[오류] \"저장 실패\" - 관리자에게 오류를 통보합니다.");
            return;
        }
        System.out.println("[시스템] 사고 접수 번호: " + reportNo);
        System.out.println("[시스템] \"정상적으로 접수되었습니다.\" | 사고 상태: '현장 조사 필요'");
        System.out.println("[시스템] AccidentReport 객체 생성 완료 | reportNo: " + accidentReport.getReportNo()
                + " | policyNumber: " + accidentReport.getPolicyNumber());
    }
}
