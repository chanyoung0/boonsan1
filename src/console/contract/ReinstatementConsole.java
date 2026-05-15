package console.contract;

import service.contract.ReinstatementService;

import java.math.BigDecimal;

import static common.ConsoleUtil.*;

// 부활 관리 콘솔 I/O — 계약관리담당자 유스케이스 입출력 전담
public class ReinstatementConsole {

    public static void run() {
        ReinstatementService service = new ReinstatementService();

        line();
        System.out.println("[유스케이스] 부활을 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        System.out.println("\n[Step 1] 부활 신청 정보 입력");
        String name = input("피보험자 이름");
        input("주민등록번호");
        String policyNo = input("증권번호");

        System.out.println("\n[시스템] 계약정보 조회 중 (증권번호: " + policyNo + ")...");
        boolean isLapsed = rnd.nextInt(10) < 7;
        System.out.println("[시스템] 조회 결과: " + (isLapsed ? "실효 계약 (부활 가능)" : "부활 불가 계약"));

        if (!isLapsed) {
            System.out.println("[시스템] \"부활 신청이 불가능한 계약입니다\" — 부활 처리를 종료합니다.");
            return;
        }

        System.out.println("[시스템] 증권번호: " + policyNo + " | 계약상태: 실효 | 미납보험료: 450,000원");

        System.out.println("\n[계약관리담당자] 부활 신청 정보를 입력합니다.");
        input("부활 사유");
        input("미납보험료 납입확인 (완료/미완료)");
        String healthChanged = input("건강상태 변동여부 (있음/없음)");
        input("최종납입일 (YYYY-MM-DD)");
        input("부활희망일 (YYYY-MM-DD)");

        System.out.println("\n[시스템] 부활 신청 내용 — 피보험자: " + name + " | 증권번호: " + policyNo);

        System.out.println("\n[계약관리담당자] '심사요청' 버튼을 누릅니다.");
        System.out.println("  >> <<include>> [심사를 요청한다] 시나리오 시작");
        String uwResult = underwritingRequestSub("부활");

        System.out.println("\n[시스템] 심사 결과: " + uwResult);
        enter();

        System.out.println("[시스템] 부활 처리 결과를 DB에 저장 중...");
        service.saveReinstatement(healthChanged, new BigDecimal("450000"));

        if (uwResult.contains("승인")) {
            System.out.println("[시스템] 부활 처리 완료. 계약상태: '유효'로 갱신");
        } else {
            System.out.println("[시스템] 심사 " + uwResult + ". 부활 처리가 거절되었습니다.");
        }
    }
}
