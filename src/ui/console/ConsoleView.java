package ui.console;

import static common.ConsoleUtil.line;

// 콘솔 출력 진입점 — 시스템 배너/메뉴/구분선 공통 출력 유틸을 캡슐화
public class ConsoleView {

    // 시스템 헤더 출력
    public void printSystemBanner() {
        System.out.println("================================================");
        System.out.println("       신동아화재 보험 관리 시스템               ");
        System.out.println("================================================");
    }

    // 메인 메뉴 출력
    public void printMainMenu() {
        System.out.println();
        System.out.println("============== 메인 메뉴 ==============");
        System.out.println("  1. 보험청약 심사");
        System.out.println("  2. 배서 관리");
        System.out.println("  3. 부활 관리");
        System.out.println("  4. 분납/수금 관리");
        System.out.println("  5. 만기계약 관리");
        System.out.println("  6. 사고 접수");
        System.out.println("  7. 손해조사");
        System.out.println("  8. 상품 설계");
        System.out.println("  9. 제지급금 관리");
        System.out.println(" 10. 보상 평가 관리");
        System.out.println(" 11. 협력업체 관리");
        System.out.println(" 12. 종료");
        System.out.println("=======================================");
        System.out.print(">> 선택: ");
    }

    // 종료 안내
    public void printExitMessage() {
        System.out.println("\n시스템을 종료합니다.");
    }

    // 오류 안내
    public void printInvalidChoice() {
        System.out.println("[오류] 올바른 번호를 입력하세요.");
    }

    // 구분선 위임
    public void printLine() {
        line();
    }
}
