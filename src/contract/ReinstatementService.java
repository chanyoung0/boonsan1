package contract;

import static common.ConsoleUtil.*;

public class ReinstatementService {

    // ======================================================
    // 3. 부활 관리
    // 액터: 계약관리담당자
    // ======================================================
    public static void run() {
        line();
        System.out.println("[유스케이스] 부활을 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        // Basic Path 1: 부활 신청 정보 입력
        System.out.println("\n[Step 1] 부활 신청 정보 입력");
        String name = input("피보험자 이름");
        input("주민등록번호");
        String policyNo = input("증권번호");

        // Basic Path 2: 계약정보 조회 — 시스템 자동 판단 (70% 실효 → 부활 가능)
        System.out.println("\n[시스템] 계약정보 조회 중 (증권번호: " + policyNo + ")...");
        boolean isLapsed = rnd.nextInt(10) < 7;
        System.out.println("[시스템] 조회 결과: " + (isLapsed ? "실효 계약 (부활 가능)" : "부활 불가 계약 (상태: 유효/해지/만기)"));

        if (!isLapsed) {
            // A1: 부활 불가
            System.out.println("[시스템] \"부활 신청이 불가능한 계약입니다\" — 부활 처리를 종료합니다.");
            return;
        }

        System.out.println("[시스템] 계약정보:");
        System.out.println("  ── 계약 기본정보 ───────────────────────────────────────");
        System.out.println("  증권번호      : " + policyNo);
        System.out.println("  계약상태      : 실효");
        System.out.println("  청약일        : 2020-01-01        승낙일      : 2020-01-03");
        System.out.println("  보험기간      : 2020-01-01 ~ 2030-01-01");
        System.out.println("  납입기간      : 10년              납입주기    : 월납");
        System.out.println("  실효일        : 2023-11-01");
        System.out.println("  ── 계약자 / 피보험자 / 수익자 ──────────────────────────");
        System.out.println("  계약자        : " + name + " (생년월일: 1985-03-15, 연락처: 010-1234-5678)");
        System.out.println("  피보험자      : " + name + " (동일)");
        System.out.println("  수익자        : " + name + " (동일)");
        System.out.println("  ── 상품 / 보장 정보 ─────────────────────────────────────");
        System.out.println("  상품명        : 자동차종합보험");
        System.out.println("  주계약내용    : 대인배상 무한, 대물배상 2,000만원, 자손 1,500만원");
        System.out.println("  특약목록      : 상해특약, 입원특약");
        System.out.println("  보험가입금액  : 3,000만원");
        System.out.println("  보험료        : 95,000원/월");
        System.out.println("  ── 납입 / 미납 정보 ─────────────────────────────────────");
        System.out.println("  납입일        : 매월 1일");
        System.out.println("  납입금액      : 95,000원");
        System.out.println("  미납여부      : 있음 (3회차 미납, 미납금액: 450,000원)");
        System.out.println("  ── 자동이체 정보 ────────────────────────────────────────");
        System.out.println("  자동이체      : 국민은행 123-456-789012 (예금주: " + name + ")");

        // Basic Path 3: 부활신청 정보 입력
        System.out.println("\n[계약관리담당자] 부활 신청 정보를 입력합니다.");
        input("부활 사유");
        input("미납보험료 납입확인 (완료/미완료)");
        input("건강상태 변동여부 (있음/없음)");
        input("최종납입일 (YYYY-MM-DD)");
        input("부활희망일 (YYYY-MM-DD)");

        System.out.println("\n[시스템] 부활 신청 내용:");
        System.out.println("  피보험자: " + name + " | 증권번호: " + policyNo);
        System.out.println("  미납보험료: 450,000원 | 신청일시: 2024-01-15 10:00:00");

        // Basic Path 5: 심사 요청 <<include>>
        System.out.println("\n[계약관리담당자] '심사요청' 버튼을 누릅니다.");
        System.out.println("  >> <<include>> [심사를 요청한다] 시나리오 시작");
        String uwResult = underwritingRequestSub("부활");

        // Basic Path 6,7: 결과 확인
        System.out.println("\n[시스템] 심사 결과: " + uwResult);
        System.out.println("[계약관리담당자] 확인 버튼을 누릅니다.");
        enter();

        // Basic Path 8: 저장 (E1)
        System.out.println("[시스템] 부활 처리 결과를 DB에 저장 중...");
        if (!simulateDbSave()) {
            System.out.println("[오류] 저장 실패. 관리자에게 오류를 통보합니다.");
            return;
        }

        if (uwResult.contains("승인")) {
            System.out.println("[시스템] 부활 처리 완료. 계약상태: '유효'로 갱신");
            System.out.println("  증권번호: " + policyNo + " | 부활처리일시: 2024-01-15 10:30:00");
        } else {
            System.out.println("[시스템] 심사 " + uwResult + ". 부활 처리가 거절되었습니다.");
        }
    }
}
