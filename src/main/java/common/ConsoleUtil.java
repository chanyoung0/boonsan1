package common;

import enums.RequestReason;
import enums.RequestStatus;
import enums.UnderwritingResultType;
import enums.UnderwritingType;
import model.underwriting.UnderwritingRequest;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Random;

public class ConsoleUtil {

    public static final Scanner sc = new Scanner(System.in);
    public static final Random rnd = new Random();

    public static String input(String label) {
        System.out.print("  " + label + ": ");
        return sc.nextLine().trim();
    }

    public static void enter() {
        System.out.print("  (계속하려면 Enter를 누르세요) ");
        sc.nextLine();
    }

    public static void line() {
        System.out.println("--------------------------------------------------");
    }

    public static String formatAmount(long amount) {
        if (amount >= 100_000_000L) {
            long eok = amount / 100_000_000L;
            long rest = amount % 100_000_000L;
            if (rest == 0) return eok + "억원";
            return eok + "억 " + String.format("%,d", rest) + "원";
        }
        return String.format("%,d", amount) + "원";
    }

    public static boolean simulateDbSave() {
        return rnd.nextInt(100) >= 5; // 5% 확률로 저장 실패
    }

    // 배서/부활 공통 서브 시나리오 <<include>>
    public static String underwritingRequestSub(String type) {
        System.out.println("\n  [심사를 요청한다]");
        System.out.println("  액터: 계약관리담당자, 언더라이터");
        System.out.println("  [계약관리담당자] 심사 요청 정보를 입력합니다. (심사유형: " + type + ")");

        UnderwritingRequest underwritingRequest = new UnderwritingRequest(
                LocalDateTime.now(),
                resolveRequestReason(type),
                UnderwritingType.GENERAL,
                RequestStatus.PENDING
        );
        underwritingRequest.requestUnderwriting();
        System.out.println("  [시스템] UnderwritingRequest 생성 완료 | 상태: "
                + underwritingRequest.getRequestStatus()
                + " | 요청사유: " + underwritingRequest.getRequestReason()
                + " | 심사유형: " + underwritingRequest.getUnderwritingType());

        // 시스템 자동: 추가 서류 필요 여부 판단 (30% 확률 서류 필요)
        boolean needsDocs = rnd.nextInt(10) < 3;
        System.out.println("  [시스템] 추가 서류 필요 여부 자동 확인 결과: " + (needsDocs ? "서류 필요" : "서류 불필요"));
        if (needsDocs) {
            System.out.println("  [시스템] \"추가 서류가 필요합니다\"");
            System.out.println("    필요 서류: 진단서, 사고확인서, 신분증 사본");
            System.out.println("  [계약관리담당자] 서류를 시스템에 첨부합니다.");
            input("  첨부 파일명");
        }

        System.out.println("  [시스템] 심사 요청이 언더라이터에게 전달되었습니다.");
        System.out.println("  [시스템] 요청상태: '심사대기'");

        System.out.println("\n  [언더라이터] 심사를 진행합니다.");
        System.out.println("  심사 결과:");
        System.out.println("    1. 승인  2. 할증  3. 거절");
        System.out.print("  >> 선택: ");
        String r = sc.nextLine().trim();
        String result = "2".equals(r) ? "할증 (보험료 10% 인상)" : "3".equals(r) ? "거절" : "승인";

        underwritingRequest.setUnderwritingResult(resolveUnderwritingResultType(r));
        underwritingRequest.setRequestStatus(RequestStatus.COMPLETED);
        underwritingRequest.registerUWResult();
        underwritingRequest.changeUWStatus();
        System.out.println("  [시스템] 요청상태: '심사완료' | 결과: " + result);
        System.out.println("  [시스템] UnderwritingRequest 상태: "
                + underwritingRequest.getRequestStatus()
                + " | 결과유형: " + underwritingRequest.getUnderwritingResult());
        System.out.println("  [시스템] 심사 요청 내역이 DB에 저장되었습니다.");
        return result;
    }

    private static RequestReason resolveRequestReason(String type) {
        if ("부활".equals(type)) {
            return RequestReason.REINSTATEMENT;
        }
        return RequestReason.ENDORSEMENT;
    }

    private static UnderwritingResultType resolveUnderwritingResultType(String choice) {
        if ("2".equals(choice)) {
            return UnderwritingResultType.SURCHARGE;
        }
        if ("3".equals(choice)) {
            return UnderwritingResultType.REJECTED;
        }
        return UnderwritingResultType.APPROVED;
    }
}
