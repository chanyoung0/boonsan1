package console.accident;

import enums.SubrogationStatus;
import model.accident.Subrogation;
import model.contract.Payout;
import service.accident.SubrogationService;
import service.contract.PayoutService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static common.ConsoleUtil.*;

// 구상 처리 콘솔 I/O — 보상관리담당자 유스케이스 입출력 전담
public class SubrogationConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 구상을 처리한다");
        System.out.println("액터: 보상관리담당자");
        line();

        List<Payout> payouts = PayoutService.getPayoutList();
        List<String> payoutIds = PayoutService.getPayoutIdList();
        if (payouts.isEmpty()) {
            System.out.println("[시스템] 등록된 제지급금이 없습니다.");
            return;
        }

        System.out.println("\n[시스템] 지급완료(PAID) 사건 목록:");
        boolean any = false;
        for (int i = 0; i < payouts.size(); i++) {
            String pid = i < payoutIds.size() ? payoutIds.get(i) : "";
            if (!"PAID".equals(PayoutService.getPayoutStatus(pid))) continue;
            any = true;
            System.out.println("  지급번호: " + pid
                    + " | 증권번호: " + PayoutService.getPolicyNumber(pid)
                    + " | 최종지급금액: " + payouts.get(i).getFinalPaymentAmount() + "원");
        }
        if (!any) {
            System.out.println("  [없음]");
            return;
        }

        String payoutId = input("구상 대상 지급번호");
        Payout payout = PayoutService.findPayoutById(payoutId);
        if (payout == null) {
            System.out.println("[오류] 해당 지급번호를 찾을 수 없습니다.");
            return;
        }
        if (!"PAID".equals(PayoutService.getPayoutStatus(payoutId))) {
            System.out.println("[오류] 지급완료(PAID) 상태가 아닙니다.");
            return;
        }
        BigDecimal paidAmount = payout.getFinalPaymentAmount();

        String offenderName = input("가해자 이름");
        String offenderContact = input("가해자 연락처");
        String faultRatioStr = input("과실비율 (% 단위, 예: 70)");
        float faultRatio = SubrogationService.parseFaultRatio(faultRatioStr);
        String deadlineStr = input("입금 기한 (YYYY-MM-DD, 빈 값이면 +14일)");
        LocalDateTime deadline = SubrogationService.resolvePaymentDeadline(deadlineStr);
        String depositAccount = input("입금 계좌");

        Subrogation subrogation = SubrogationService.createSubrogation(
                null, offenderName, offenderContact, faultRatio,
                paidAmount, deadline, depositAccount);
        System.out.println("[시스템] 구상금액 계산 결과: " + subrogation.getPaymentAmount() + "원"
                + " (지급금액 " + paidAmount + " × 과실비율 " + faultRatio + "%)");

        SubrogationStatus statusAfterClaim = SubrogationService.sendClaim(subrogation);
        System.out.println("[시스템] 구상 청구서 발송 완료 | 상태: " + statusAfterClaim);

        String depositAnswer = input("입금이 완료되었습니까? (Y/N)");
        SubrogationStatus statusAfterDeposit = SubrogationService.confirmDeposit(subrogation, depositAnswer);
        System.out.println("[시스템] 입금 확인 처리 | 상태: " + statusAfterDeposit);

        System.out.println("[시스템] 구상 정보를 DB에 저장 중...");
        String savedId = SubrogationService.saveSubrogation(subrogation);
        if (savedId == null) {
            System.out.println("[오류] 구상 DB 저장에 실패했습니다.");
            return;
        }
        System.out.println("[시스템] 구상 처리 완료"
                + "\n  구상번호: " + savedId
                + "\n  가해자: " + subrogation.getOffenderName() + " (연락처: " + subrogation.getOffenderContact() + ")"
                + "\n  구상금액: " + subrogation.getPaymentAmount() + "원"
                + "\n  입금기한: " + subrogation.getPaymentDeadline()
                + "\n  최종 상태: " + SubrogationService.findStatusById(savedId));
    }
}
