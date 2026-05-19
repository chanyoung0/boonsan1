package console.contract;

import model.contract.PaymentCollection;
import service.contract.PaymentCollectionService;

import java.util.List;

import static common.ConsoleUtil.*;

// 분납/수금 관리 콘솔 I/O
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

        System.out.println("자동이체 처리 결과를 입력합니다. 1. 전체 성공  2. 일부 실패");
        System.out.print(">> 선택: ");
        String resultChoice = sc.nextLine().trim();

        PaymentCollection paymentCollection = PaymentCollectionService.createCollectionResult(resultChoice);
        boolean allSuccess = PaymentCollectionService.isCollectionFullySuccessful(paymentCollection);
        System.out.println("[시스템] 처리 결과: " + (allSuccess ? "전체 성공" : "일부 실패 발생"));
        System.out.println(PaymentCollectionService.createCollectionResultSummary(paymentCollection));

        List<String> savedCollectionIds = PaymentCollectionService.saveCollectionResults(resultChoice);
        if (savedCollectionIds.isEmpty()) {
            System.out.println("[오류] 수금 처리 결과를 DB에 저장하지 못했습니다.");
            return;
        }
        System.out.println(PaymentCollectionService.createSavedCollectionSummary(savedCollectionIds));

        if (!allSuccess) {
            System.out.println("[시스템] 성공 2건 / 실패 1건 | 미납 계약: P2024-009012 (이철수, 220,000원)");
            System.out.println(PaymentCollectionService.createUnpaidNoticeSummary(paymentCollection));
            System.out.println("[시스템] P2024-009012 계약상태는 현재 콘솔 흐름에서 '미납'으로 처리합니다.");

            String unpaidCollectionId = PaymentCollectionService.findFirstUnpaidCollectionId(savedCollectionIds);
            System.out.print("\n[시스템] 미납 지속 계약을 이관 처리하시겠습니까? (Y/N): ");
            if ("Y".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("  1. 방문수금  2. 이관");
                System.out.print(">> 선택: ");
                String transferChoice = sc.nextLine().trim();
                if (PaymentCollectionService.transferUnpaidCollection(unpaidCollectionId, transferChoice)) {
                    System.out.println(PaymentCollectionService.createTransferSummary(transferChoice));
                    System.out.println("[시스템] 수금번호 " + unpaidCollectionId + " 상태가 TRANSFERRED로 갱신되었습니다.");
                } else {
                    System.out.println("[오류] 미납 수금 이관 상태 갱신에 실패했습니다.");
                }
            }
        } else {
            System.out.println("[시스템] 성공 3건 / 총 수금액: 468,000원");
        }

        enter();
        System.out.println(PaymentCollectionService.createCollectionListSummary());
        System.out.println("[시스템] 수금 처리 결과가 DB에 저장되었습니다.");
    }
}
