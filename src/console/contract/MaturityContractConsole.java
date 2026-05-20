package console.contract;

import enums.DeliveryMethod;
import model.contract.MaturityNotice;
import service.contract.MaturityContractService;

import static common.ConsoleUtil.*;

// 만기계약 관리 콘솔 I/O — 계약관리담당자 유스케이스 입출력 전담
public class MaturityContractConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 만기계약을 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        System.out.println("\n[시스템] 만기일이 도래한 계약 목록을 자동 추출 중...");
        System.out.println("[시스템] 만기대상 계약 목록:");
        System.out.println("  1 | P2019-000123 | 박민준 | 2024-01-31 | 3,500,000원");
        System.out.println("  2 | P2019-000456 | 최수진 | 2024-01-31 | 5,200,000원");
        System.out.println("[시스템] 만기 안내장 자동 발송 완료 (SMS, 이메일)");
        MaturityNotice maturityNotice = MaturityContractService.createMaturityNotice(DeliveryMethod.SMS);
        System.out.println(MaturityContractService.createNoticeDeliverySummary(maturityNotice));
        System.out.println("[시스템] 재계약 의사 확인 대상 목록:");
        System.out.println("  1 | P2019-000123 | 박민준 | 2024-01-31 | 안내발송일시: " + maturityNotice.getSentAt());
        System.out.println("  2 | P2019-000456 | 최수진 | 2024-01-31 | 안내발송일시: " + maturityNotice.getSentAt());

        System.out.println("\n[계약관리담당자] 재계약 의사 확인 결과를 입력합니다.");
        System.out.println("  1. 재계약 의사 있음  2. 재계약 의사 없음  3. 회신 없음 (기한 초과)");
        System.out.print(">> 선택: ");
        String intention = sc.nextLine().trim();

        String result = MaturityContractService.applyRenewalIntention(maturityNotice, intention);
        System.out.println("[시스템] " + result);
        System.out.println(MaturityContractService.createRenewalCheckSummary(maturityNotice));
        System.out.println("[시스템] 만기 처리 결과를 DB에 저장 중...");
        String noticeId = MaturityContractService.saveMaturityResult(maturityNotice);
        if (noticeId == null) {
            System.out.println("[오류] 저장 실패. 관리자에게 오류를 통보합니다.");
            return;
        }
        System.out.println("[시스템] 만기 처리 결과가 DB에 저장되었습니다. | 안내번호: " + noticeId);
    }
}
