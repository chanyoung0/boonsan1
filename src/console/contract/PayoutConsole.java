package console.contract;

import model.contract.Payout;
import service.contract.PayoutService;

import static common.ConsoleUtil.*;

// 제지급금 관리 콘솔 I/O — 계약관리담당자 유스케이스 입출력 전담
public class PayoutConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 제지급금을 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        System.out.println("\n[계약관리담당자] 지급 요청 정보를 입력합니다.");
        input("피보험자 이름");
        input("주민등록번호");
        String policyNo = input("증권번호");
        System.out.println("  지급유형: 1. 만기환급금  2. 중도인출금  3. 해지환급금  4. 배당금");
        System.out.print("  >> 선택: ");
        String paymentTypeChoice = sc.nextLine().trim();
        String paymentTypeLabel  = PayoutService.resolvePaymentTypeLabel(paymentTypeChoice);

        System.out.println("\n[시스템] 계약정보 조회 중 (증권번호: " + policyNo + ")...");
        boolean payable = PayoutService.isPayable();
        if (!payable) {
            System.out.println("[시스템] \"지급이 불가능한 계약입니다\" | 계약상태: 실효");
            System.out.println("[계약관리담당자] 확인 버튼을 누릅니다.");
            enter();
            return;
        }

        System.out.println("[시스템] 계약정보: " + policyNo + " | 계약상태: 유효 | 보험기간: 5년 | 납입회차: 36회 | 계좌: 신한은행 110-123-456789");

        System.out.println("\n[계약관리담당자] '지급요청 확인' 버튼을 누릅니다.");
        enter();

        java.math.BigDecimal amount = PayoutService.calculateAmount(paymentTypeChoice);
        System.out.println("[시스템] 지급금 자동 산출 결과:");
        System.out.println("  지급유형: " + paymentTypeLabel + " | 산출금액: " + String.format("%,d", amount.longValue()) + "원");
        System.out.println("  산출기준: 계약별 약관 적용 | 공제항목: 없음 | 최종지급금액: " + String.format("%,d", amount.longValue()) + "원");

        System.out.println("\n지급금 산출 결과를 승인하시겠습니까?  1. 승인  2. 반려");
        System.out.print(">> 선택: ");
        String approval = sc.nextLine().trim();

        if ("2".equals(approval)) {
            String rejectReason = input("반려 사유");
            System.out.println("[시스템] 반려 결과 (증권번호: " + policyNo + ", 반려사유: " + rejectReason + ")가 DB에 저장되었습니다.");
            return;
        }

        String processor = input("처리 사원번호");

        System.out.println("\n[시스템] 계좌로 지급금을 자동 이체 중...");
        System.out.println("[시스템] 이체 완료: " + String.format("%,d", amount.longValue()) + "원 → 신한은행 110-123-456789");
        System.out.println("[시스템] 지급안내장 자동 발송 완료 (증권번호: " + policyNo
            + " | 지급유형: " + paymentTypeLabel + " | 지급금액: " + String.format("%,d", amount.longValue()) + "원)");

        System.out.println("\n[계약관리담당자] '확인' 버튼을 누릅니다.");
        enter();

        System.out.println("[시스템] 지급 처리 결과를 DB에 저장 중...");
        Payout payout = PayoutService.savePayout(processor, paymentTypeChoice, amount);
        System.out.println("[시스템] 지급 처리 결과 저장 완료 | 계약정보 갱신 완료");
    }
}
