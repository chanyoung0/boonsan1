package console.contract;

import model.contract.PaymentCollection;
import model.contract.Transfer;
import model.contract.UnpaidNotice;
import service.contract.PaymentCollectionService;

import static common.ConsoleUtil.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 분납/수금 관리 콘솔 I/O — 계약관리담당자 유스케이스 입출력 전담
public class PaymentCollectionConsole {

    // 콘솔이 시뮬레이션 상 사용하는 미납 계약의 증권번호.
    private static final String UNPAID_POLICY_NUMBER = "P2024-009012";

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
        System.out.println("  3 | " + UNPAID_POLICY_NUMBER + " | 이철수 | 220,000원 | 2024-01-15");

        System.out.println("\n[계약관리담당자] '일괄처리' 버튼을 누릅니다.");
        enter();
        System.out.println("[시스템] 자동이체 일괄 실행 중...");

        System.out.println("자동이체 처리 결과를 입력합니다: 1. 전체 성공  2. 일부 실패");
        System.out.print(">> 선택: ");
        String resultChoice = sc.nextLine().trim();
        PaymentCollection paymentCollection = PaymentCollectionService.createCollectionResult(resultChoice);
        boolean allSuccess = PaymentCollectionService.isCollectionFullySuccessful(paymentCollection);
        System.out.println("[시스템] 처리 결과: " + (allSuccess ? "전체 성공" : "일부 실패 발생"));
        System.out.println(PaymentCollectionService.createCollectionResultSummary(paymentCollection));

        if (!PaymentCollectionService.savePaymentCollection(paymentCollection, UNPAID_POLICY_NUMBER)) {
            System.out.println("[오류] 수금 처리 결과 DB 저장 실패. 후속 처리는 건너뜁니다.");
            return;
        }
        String paymentCollectionId = paymentCollection.getPaymentCollectionId();

        if (!allSuccess) {
            System.out.println("[시스템] 성공 2건 / 실패 1건 | 미납 계약: " + UNPAID_POLICY_NUMBER + " (이철수, 220,000원)");
            UnpaidNotice unpaidNotice = PaymentCollectionService.createAndSaveUnpaidNotice(
                    paymentCollectionId,
                    BigDecimal.valueOf(220_000L),
                    LocalDateTime.now().plusDays(14)
            );
            if (unpaidNotice == null) {
                System.out.println("[오류] 미납 안내장 DB 저장 실패.");
            } else {
                System.out.println(PaymentCollectionService.createUnpaidNoticeSummary(paymentCollection));
                System.out.println("[시스템] 미납안내번호: " + unpaidNotice.getUnpaidNoticeId()
                        + " | 분납수금번호: " + paymentCollectionId);
            }
            System.out.println("[시스템] " + UNPAID_POLICY_NUMBER + " 계약상태: '미납'");

            System.out.print("\n[시스템] 미납 지속 계약 이관 처리하시겠습니까? (Y/N): ");
            if ("Y".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("  1. 방문수금  2. 해지처리");
                System.out.print(">> 선택: ");
                String tType = sc.nextLine().trim();
                Transfer transfer = PaymentCollectionService.createAndSaveTransfer(paymentCollectionId, tType);
                if (transfer == null) {
                    System.out.println("[오류] 이관 DB 저장 실패.");
                } else {
                    System.out.println(PaymentCollectionService.createTransferSummary(tType));
                    System.out.println("[시스템] 이관번호: " + transfer.getTransferId());
                }
            }
        } else {
            System.out.println("[시스템] 성공 3건 / 총 수금액: 468,000원");
        }

        enter();
        System.out.println("[시스템] 수금 처리 결과가 DB에 저장되고 계약정보가 갱신되었습니다. | 분납수금번호: "
                + paymentCollectionId);
    }
}
