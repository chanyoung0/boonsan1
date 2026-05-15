package console.accident;

import service.accident.AccidentReportService;

import static common.ConsoleUtil.*;

// 사고 접수 콘솔 I/O — 보험가입자/보험사 직원 유스케이스 입출력 전담
public class AccidentReportConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 사고를 접수한다");
        System.out.println("액터: 보험가입자, 보험사 직원");
        line();

        System.out.println("\n[보험가입자] '사고 접수' 버튼을 누릅니다.");
        System.out.println("[시스템] 사고 접수 화면:");
        input("보험 증권번호");
        input("사고 일시 (YYYY-MM-DD HH:MM)");
        input("사고 경위");
        input("피해 내용 (예: 차량 파손, 부상)");

        System.out.println("[보험가입자] '접수' 버튼을 누릅니다.");
        enter();

        System.out.println("[시스템] 계약 정보:");
        System.out.println("  피보험자명: 홍길동 | 보험 종류: 자동차보험 | 보장 범위: 대인/대물/자손");

        System.out.println("\n서류 제출 방법: 1. 지금 업로드  2. 나중에 제출");
        System.out.print(">> 선택: ");
        String docChoice = sc.nextLine().trim();

        if (AccidentReportService.isDocumentDeferred(docChoice)) {
            System.out.println("[시스템] '서류 미제출' 상태로 접수 처리합니다.");
        } else {
            input("  사고경위서 파일명");
            input("  진단서 파일명 (없으면 Enter)");
            input("  청구서류 파일명");
            enter();
        }

        System.out.println("[시스템] 접수 내용을 DB에 저장 중...");
        if (!simulateDbSave()) {
            System.out.println("[오류] \"저장 실패\" - 관리자에게 오류를 통보합니다.");
            return;
        }
        String reportNo = AccidentReportService.generateReportNo();
        System.out.println("[시스템] 사고 접수 번호: " + reportNo);
        System.out.println("[시스템] \"정상적으로 접수되었습니다.\" | 사고 상태: '현장 조사 필요'");
    }
}
