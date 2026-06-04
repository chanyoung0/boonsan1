package com.boonsan.service.accident;

import static com.boonsan.common.ConsoleUtil.*;

public class AccidentReportService {

    // ======================================================
    // 6. 사고 접수
    // 액터: 보험가입자, 보험사 직원
    // ======================================================
    public static void run() {
        line();
        System.out.println("[유스케이스] 사고를 접수한다");
        System.out.println("액터: 보험가입자, 보험사 직원");
        line();

        // Basic Path 1~2: 사고 접수 화면
        System.out.println("\n[보험가입자] '사고 접수' 버튼을 누릅니다.");
        System.out.println("[시스템] 사고 접수 화면:");
        input("보험 증권번호");
        input("사고 일시 (YYYY-MM-DD HH:MM)");
        input("사고 경위");
        input("피해 내용 (예: 차량 파손, 부상)");

        // Basic Path 3: 접수 버튼
        System.out.println("[보험가입자] '접수' 버튼을 누릅니다.");
        enter();

        // Basic Path 4: 계약정보 확인
        System.out.println("[시스템] 계약 정보:");
        System.out.println("  피보험자명: 홍길동 | 보험 종류: 자동차보험 | 보장 범위: 대인/대물/자손");

        // Basic Path 5: 서류 업로드 — 보험가입자 판단 (A1 분기)
        System.out.println("\n서류 제출 방법:");
        System.out.println("  1. 지금 서류 업로드");
        System.out.println("  2. 나중에 제출");
        System.out.print(">> 선택: ");
        String docChoice = sc.nextLine().trim();

        if ("2".equals(docChoice)) {
            // A1: 서류 나중에 제출
            System.out.println("[보험가입자] '서류 나중에 제출' 버튼을 누릅니다.");
            System.out.println("[시스템] '서류 미제출' 상태로 접수 처리합니다.");
        } else {
            System.out.println("[보험가입자] 사고 관련 서류를 업로드합니다.");
            input("  사고경위서 파일명");
            input("  진단서 파일명 (없으면 Enter)");
            input("  청구서류 파일명");
            System.out.println("[보험가입자] '제출' 버튼을 누릅니다.");
            enter();
        }

        // Basic Path 6: 저장 및 사고번호 생성 (E1)
        System.out.println("[시스템] 접수 내용을 DB에 저장 중...");
        if (!simulateDbSave()) {
            System.out.println("[오류] \"저장 실패\" - 관리자에게 오류를 통보합니다.");
            return;
        }
        String reportNo = "ACC-2024-" + String.format("%06d", rnd.nextInt(999999) + 1);
        System.out.println("[시스템] 사고 접수 번호: " + reportNo);
        System.out.println("[시스템] \"정상적으로 접수되었습니다.\"");
        System.out.println("[시스템] 사고 상태: '현장 조사 필요'");
    }
}
