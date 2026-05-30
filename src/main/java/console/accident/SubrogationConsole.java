package console.accident;

import enums.PaymentStatus;
import enums.SubrogationStatus;
import model.accident.InsurancePayment;
import model.accident.Subrogation;
import model.contract.Payout;
import service.accident.SubrogationService;
import service.contract.PayoutService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static common.ConsoleUtil.*;

// 구상 처리 콘솔 I/O — 보상담당자 유스케이스 입출력 전담
public class SubrogationConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 구상을 처리한다");
        System.out.println("액터: 보상담당자");
        line();

        List<Payout> paidPayouts = collectPaidPayouts();
        if (paidPayouts.isEmpty()) {
            System.out.println("[시스템] 지급완료된 사건이 없어 구상 처리할 대상이 없습니다.");
            return;
        }

        System.out.println("\n[Step 1] 지급완료 사건 목록");
        List<String> payoutIdList = PayoutService.getPayoutIdList();
        for (int i = 0; i < paidPayouts.size(); i++) {
            Payout payout = paidPayouts.get(i);
            String payoutId = i < payoutIdList.size() ? payoutIdList.get(i) : PayoutService.getPayoutId(payout);
            System.out.println("  " + (i + 1)
                    + ". 지급번호: " + payoutId
                    + " | 증권번호: " + PayoutService.getPolicyNumber(payoutId)
                    + " | 지급금액: " + payout.getFinalPaymentAmount()
                    + " | 지급일시: " + payout.getPaidAt());
        }

        Payout selectedPayout;
        String selectedPayoutId;
        while (true) {
            System.out.print("\n>> 구상 대상 사건 번호를 선택하세요: ");
            String idx = sc.nextLine().trim();
            try {
                int n = Integer.parseInt(idx);
                if (n >= 1 && n <= paidPayouts.size()) {
                    selectedPayout = paidPayouts.get(n - 1);
                    selectedPayoutId = n - 1 < payoutIdList.size()
                            ? payoutIdList.get(n - 1)
                            : PayoutService.getPayoutId(selectedPayout);
                    break;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("[오류] 올바른 번호를 입력하세요.");
        }

        System.out.println("\n[Step 2] 가해자 및 사고 정보 입력");
        String offenderName = input("가해자 이름");
        String offenderContact = input("가해자 연락처");
        float faultRatio = SubrogationService.parseFaultRatio(input("과실비율 (% — 예: 60)"));

        BigDecimal paidAmount = selectedPayout.getFinalPaymentAmount();
        BigDecimal subrogationAmount = SubrogationService.calculateSubrogationAmount(paidAmount, faultRatio);
        System.out.println("[시스템] 구상금액 계산: 지급액 " + paidAmount
                + "원 × 과실비율 " + faultRatio + "% = " + subrogationAmount + "원");

        String depositAccount = input("입금 계좌 (예: 신한 110-123-456789)");
        LocalDateTime paymentDeadline = SubrogationService.resolvePaymentDeadline(input("입금 기한 (YYYY-MM-DD, Enter 시 +14일)"));

        InsurancePayment payment = buildPaymentFromPayout(selectedPayoutId, selectedPayout);

        Subrogation subrogation = SubrogationService.createSubrogation(
                payment,
                offenderName,
                offenderContact,
                faultRatio,
                paidAmount,
                paymentDeadline,
                depositAccount
        );

        System.out.println("\n[Step 3] 구상 청구서 발송");
        SubrogationStatus afterClaim = SubrogationService.sendClaim(subrogation);
        System.out.println("[시스템] 구상 청구서 발송 완료 | 상태: " + afterClaim);

        System.out.println("\n[Step 4] 입금 확인");
        String depositAnswer = input("입금이 확인되었습니까? (Y/N)");
        SubrogationStatus afterDeposit = SubrogationService.confirmDeposit(subrogation, depositAnswer);
        System.out.println("[시스템] 최종 구상 상태: " + afterDeposit);

        System.out.println("\n[Step 5] 구상 정보 DB 저장");
        String savedId = SubrogationService.saveSubrogation(subrogation);
        if (savedId == null) {
            System.out.println("[오류] 구상 저장에 실패했습니다.");
            return;
        }
        System.out.println("[시스템] 구상 저장 완료 | 구상번호: " + savedId
                + " | 가해자: " + subrogation.getOffenderName()
                + " | 구상금액: " + subrogation.getPaymentAmount() + "원"
                + " | 상태: " + SubrogationService.findStatusById(savedId));
    }

    private static List<Payout> collectPaidPayouts() {
        List<Payout> allPayouts = PayoutService.getPayoutList();
        List<String> payoutIdList = PayoutService.getPayoutIdList();
        List<Payout> result = new ArrayList<>();
        for (int i = 0; i < allPayouts.size(); i++) {
            String payoutId = i < payoutIdList.size() ? payoutIdList.get(i) : PayoutService.getPayoutId(allPayouts.get(i));
            if ("PAID".equals(PayoutService.getPayoutStatus(payoutId))) {
                result.add(allPayouts.get(i));
            }
        }
        return result;
    }

    private static InsurancePayment buildPaymentFromPayout(String payoutId, Payout payout) {
        InsurancePayment payment = new InsurancePayment();
        payment.setPaymentId(payoutId);
        payment.setFinalSettlementAmount(payout.getFinalPaymentAmount());
        payment.setPaidAt(payout.getPaidAt());
        payment.setPaymentStatus(PaymentStatus.PAID);
        return payment;
    }
}
