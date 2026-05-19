package console.contract;

import enums.CalculationBasis;
import enums.PaymentType;
import model.contract.Payout;
import service.contract.PayoutService;

import java.math.BigDecimal;
import java.util.List;

import static common.ConsoleUtil.*;

public class PayoutConsole {

    public static void run() {
        while (true) {
            line();
            System.out.println("[제지급금 관리]");
            System.out.println("1. 제지급금 등록");
            System.out.println("2. 제지급금 승인");
            System.out.println("3. 제지급금 지급 처리");
            System.out.println("4. 제지급금 취소");
            System.out.println("5. 제지급금 목록 조회");
            System.out.println("6. 이전 메뉴");
            line();
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    registerPayout();
                    break;
                case "2":
                    approvePayout();
                    break;
                case "3":
                    processPayout();
                    break;
                case "4":
                    cancelPayout();
                    break;
                case "5":
                    printPayoutList();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static void registerPayout() {
        System.out.println("\n[유스케이스] 제지급금을 관리한다 - 등록");
        String policyNumber = input("증권번호");
        String contractStatus = input("계약상태(유효/만기/기타)");
        System.out.println("[시스템] 계약정보 조회 결과 | 증권번호: " + policyNumber + " | 계약상태: " + contractStatus);
        if (!PayoutService.isPayableContractStatus(contractStatus)) {
            System.out.println("[시스템] 지급이 불가능한 계약입니다. 계약상태: " + contractStatus);
            return;
        }
        String processor = input("처리자");
        PaymentType paymentType = selectPaymentType();
        CalculationBasis calculationBasis = selectCalculationBasis();
        BigDecimal baseAmount = inputAmount("기준 금액");
        BigDecimal deductionAmount = inputAmount("공제 금액");
        String deductionItem = input("공제 항목");

        Payout payout = PayoutService.createPayout(
                policyNumber,
                processor,
                paymentType,
                calculationBasis,
                baseAmount,
                deductionAmount,
                deductionItem
        );
        if (payout == null) {
            System.out.println("[오류] 제지급금 등록에 실패했습니다. DB 연결을 확인하세요.");
            return;
        }

        System.out.println("[시스템] 제지급금 등록 완료");
        System.out.println("  지급번호: " + payout.getPayoutId());
        System.out.println("  증권번호: " + payout.getPolicyNumber());
        System.out.println("  최종 지급금액: " + formatMoney(payout.getFinalPaymentAmount()));
        System.out.println("  상태: " + PayoutService.getPayoutStatus(payout));
        System.out.println(PayoutService.createPayoutCalculationSummary(payout));
    }

    private static void approvePayout() {
        System.out.println("\n[제지급금 승인]");
        printPayoutList();
        String payoutId = input("승인할 지급번호");
        Payout payout = PayoutService.findPayoutById(payoutId);
        if (payout == null) {
            System.out.println("[오류] 해당 지급번호를 찾을 수 없습니다.");
            return;
        }
        if (payout.isCancelled()) {
            System.out.println("[오류] 취소된 제지급금은 승인할 수 없습니다.");
            return;
        }

        System.out.println("승인 처리: 1. 승인  2. 반려");
        System.out.print(">> 선택: ");
        String approvalChoice = sc.nextLine().trim();
        if ("2".equals(approvalChoice)) {
            String rejectionReason = input("반려 사유");
            Payout rejected = PayoutService.cancelPayout(payoutId, rejectionReason);
            if (rejected == null) {
                System.out.println("[오류] 반려 처리에 실패했습니다.");
                return;
            }
            System.out.println(PayoutService.createPayoutRejectionMessage(rejected, rejectionReason));
            return;
        }

        Payout approved = PayoutService.approvePayout(payoutId);
        if (approved == null) {
            System.out.println("[오류] 해당 지급번호를 승인할 수 없는 상태입니다.");
            return;
        }

        System.out.println("[시스템] 제지급금 승인 완료");
        System.out.println("  승인일시: " + approved.getApprovedAt());
        System.out.println("  상태: " + PayoutService.getPayoutStatus(approved));
    }

    private static void processPayout() {
        System.out.println("\n[제지급금 지급 처리]");
        printPayoutList();
        String payoutId = input("지급 처리할 지급번호");
        Payout payout = PayoutService.findPayoutById(payoutId);
        if (payout == null) {
            System.out.println("[오류] 해당 지급번호를 찾을 수 없습니다.");
            return;
        }
        if (payout.isCancelled()) {
            System.out.println("[오류] 취소된 제지급금은 지급 처리할 수 없습니다.");
            return;
        }
        if (payout.getApprovedAt() == null) {
            System.out.println("[오류] 승인 후 지급 처리할 수 있습니다.");
            return;
        }

        Payout processed = PayoutService.processPayout(payoutId);
        if (processed == null) {
            System.out.println("[오류] 지급 처리에 실패했습니다.");
            return;
        }
        System.out.println("[시스템] 제지급금 지급 처리 완료");
        System.out.println("  지급일시: " + processed.getPaidAt());
        System.out.println("  상태: " + PayoutService.getPayoutStatus(processed));
        System.out.println(PayoutService.createPaymentNotice(processed));
    }

    private static void cancelPayout() {
        System.out.println("\n[제지급금 취소]");
        printPayoutList();
        String payoutId = input("취소할 지급번호");
        Payout payout = PayoutService.findPayoutById(payoutId);
        if (payout == null) {
            System.out.println("[오류] 해당 지급번호를 찾을 수 없습니다.");
            return;
        }
        String reason = input("취소/반려 사유");
        Payout cancelled = PayoutService.cancelPayout(payoutId, reason);
        if (cancelled == null) {
            System.out.println("[오류] 취소 처리에 실패했습니다.");
            return;
        }

        System.out.println("[시스템] 제지급금 취소 처리 완료");
        System.out.println("  지급번호: " + cancelled.getPayoutId());
        System.out.println("  상태: " + PayoutService.getPayoutStatus(cancelled));
        System.out.println(PayoutService.createCancellationMessage(cancelled, reason));
    }

    private static void printPayoutList() {
        System.out.println("\n[제지급금 목록 조회]");
        List<Payout> payoutList = PayoutService.getPayoutList();
        if (payoutList.isEmpty()) {
            System.out.println("[시스템] 등록된 제지급금이 없습니다.");
            return;
        }

        for (Payout payout : payoutList) {
            System.out.println("  지급번호: " + payout.getPayoutId()
                    + " | 증권번호: " + payout.getPolicyNumber()
                    + " | 지급유형: " + payout.getPaymentType()
                    + " | 산정기준: " + payout.getCalculationBasis()
                    + " | 기준금액: " + formatMoney(payout.getCalculatedAmount())
                    + " | 최종지급금액: " + formatMoney(payout.getFinalPaymentAmount())
                    + " | 상태: " + PayoutService.getPayoutStatus(payout));
        }
    }

    private static PaymentType selectPaymentType() {
        while (true) {
            System.out.println("지급 유형: 1. LUMP_SUM  2. INSTALLMENT  3. LOAN_SETTLEMENT");
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": return PaymentType.LUMP_SUM;
                case "2": return PaymentType.INSTALLMENT;
                case "3": return PaymentType.LOAN_SETTLEMENT;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static CalculationBasis selectCalculationBasis() {
        while (true) {
            System.out.println("산정 기준: 1. MATURITY_REFUND  2. MID_SURRENDER  3. SURRENDER  4. DIVIDEND");
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": return CalculationBasis.MATURITY_REFUND;
                case "2": return CalculationBasis.MID_SURRENDER;
                case "3": return CalculationBasis.SURRENDER;
                case "4": return CalculationBasis.DIVIDEND;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static BigDecimal inputAmount(String label) {
        while (true) {
            String value = input(label + " (원)").replace(",", "").trim();
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.println("[오류] 숫자로 입력하세요.");
            }
        }
    }

    private static String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0원";
        }
        return formatAmount(amount.longValue());
    }
}
