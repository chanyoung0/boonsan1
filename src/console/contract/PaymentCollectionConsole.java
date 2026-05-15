package console.contract;

import service.contract.PaymentCollectionService;

import static common.ConsoleUtil.*;

// 분납/수금 관리 콘솔 I/O — 계약관리담당자 유스케이스 입출력 전담
public class PaymentCollectionConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 분납/수금을 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        System.out.println("\n[계약관리담당자] '수금 실행' 버튼을 누릅니다.");
        enter();

        System.out.println("[시스템] 납입기일이 도래한 수금대상 계약을 추출 중...");
        System.out.println("[시스템] 수금대상 계약 목록:");
        System.out.println("  1 | P2024-001234 | 홍길동 | 150,000원 | 2024-01-15");
        System.out.println("  2 | P2024-005678 | 김영희 |  98,000원 | 2024-01-15");
        System.out.println("  3 | P2024-009012 | 이철수 | 220,000원 | 2024-01-15");

        System.out.println("\n[계약관리담당자] '일괄처리' 버튼을 누릅니다.");
        enter();
        System.out.println("[시스템] 자동이체 일괄 실행 중...");

        boolean allSuccess = rnd.nextInt(10) < 7;
        System.out.println("[시스템] 처리 결과: " + (allSuccess ? "전체 성공" : "일부 실패 발생"));

        if (!allSuccess) {
            System.out.println("[시스템] 성공 2건 / 실패 1건 | 미납 계약: P2024-009012 (이철수, 220,000원)");
            System.out.println("[시스템] 미납 안내장 발송 완료 (납입기한: 2024-01-31)");
            System.out.println("[시스템] P2024-009012 계약상태: '미납'");

            System.out.print("\n[시스템] 미납 지속 계약 이관 처리하시겠습니까? (Y/N): ");
            if ("Y".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("  1. 방문수금  2. 해지처리");
                System.out.print(">> 선택: ");
                String tType = sc.nextLine().trim();
                String label = PaymentCollectionService.determineTransferType(tType);
                System.out.println("[시스템] 이관 처리 완료: " + label);
            }
        } else {
            System.out.println("[시스템] 성공 3건 / 총 수금액: 468,000원");
        }

        enter();
        System.out.println("[시스템] 수금 처리 결과가 DB에 저장되고 계약정보가 갱신되었습니다.");
    }
}
