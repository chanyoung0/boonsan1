package contract;

import static common.ConsoleUtil.*;

public class EndorsementService {

    // ======================================================
    // 2. 배서 관리
    // 액터: 계약관리담당자
    // ======================================================
    public static void run() {
        line();
        System.out.println("[유스케이스] 배서를 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        // Basic Path 1: 배서 신청 정보 입력
        System.out.println("\n[Step 1] 배서 신청 정보 입력");
        input("피보험자 이름");
        input("주민등록번호");
        String policyNo = input("증권번호");
        System.out.println("  배서유형:");
        System.out.println("  1. 보험가입금액 변경  2. 납입주기 변경  3. 특약 추가  4. 특약 삭제");
        System.out.print(">> 선택: ");
        String endorsementType = sc.nextLine().trim();

        // Basic Path 2: 계약정보 조회 — 시스템 자동 판단 (80% 유효 계약)
        System.out.println("\n[시스템] 계약정보 조회 중 (증권번호: " + policyNo + ")...");
        boolean isValidContract = rnd.nextInt(10) < 8;
        System.out.println("[시스템] 조회 결과: " + (isValidContract ? "유효 계약" : "유효하지 않은 계약 (상태: 실효)"));

        if (!isValidContract) {
            // A1: 유효하지 않은 계약
            System.out.println("[시스템] \"유효하지 않은 계약입니다\" — 배서 처리를 종료합니다.");
            return;
        }

        System.out.println("[시스템] 계약정보:");
        System.out.println("  ── 계약 기본정보 ───────────────────────────────────────");
        System.out.println("  증권번호      : " + policyNo);
        System.out.println("  계약상태      : 유효");
        System.out.println("  청약일        : 2020-01-01        승낙일      : 2020-01-03");
        System.out.println("  보험기간      : 2020-01-01 ~ 2030-01-01");
        System.out.println("  납입기간      : 10년              납입주기    : 월납");
        System.out.println("  ── 계약자 / 피보험자 / 수익자 ──────────────────────────");
        System.out.println("  계약자        : 홍길동 (생년월일: 1985-03-15, 연락처: 010-1234-5678)");
        System.out.println("  피보험자      : 홍길동 (동일)");
        System.out.println("  수익자        : 홍길동 (동일)");
        System.out.println("  ── 상품 / 보장 정보 ─────────────────────────────────────");
        System.out.println("  상품명        : 자동차종합보험");
        System.out.println("  주계약내용    : 대인배상 무한, 대물배상 2,000만원, 자손 1,500만원");
        System.out.println("  특약목록      : 상해특약, 입원특약, 긴급출동특약");
        System.out.println("  보험가입금액  : 5,000만원");
        System.out.println("  보험료        : 120,000원/월");
        System.out.println("  ── 납입 / 미납 정보 ─────────────────────────────────────");
        System.out.println("  납입일        : 매월 15일");
        System.out.println("  납입금액      : 120,000원");
        System.out.println("  미납여부      : 없음");
        System.out.println("  ── 자동이체 정보 ────────────────────────────────────────");
        System.out.println("  자동이체      : 신한은행 110-123-456789 (예금주: 홍길동)");

        // Basic Path 3: 배서항목 입력
        System.out.println("\n[계약관리담당자] 변경할 배서항목을 입력합니다.");
        input("변경 내용");
        input("변경 사유");

        // 배서유형에 따른 심사 필요 여부 — 시스템 자동 판단
        // 가입금액 변경(1), 특약 추가(3) → 위험 변동 → 심사 필요
        // 납입주기 변경(2), 특약 삭제(4) → 단순 조건 변경 → 심사 불필요
        boolean needsReview = "1".equals(endorsementType) || "3".equals(endorsementType);
        System.out.println("\n[시스템] 배서 신청 내용 확인 완료.");
        System.out.println("[시스템] 배서유형 분석 결과 — 심사 필요 여부: "
                + (needsReview ? "필요 (위험 변동 배서)" : "불필요 (단순 조건 변경)"));

        if (!needsReview) {
            // A2: 심사 불필요
            System.out.println("[시스템] 배서 신청 내용을 처리합니다 (심사 생략).");
            System.out.println("[시스템] 배서 내용이 DB에 저장되고 계약정보가 갱신됩니다.");
            System.out.println("[시스템] 배서 처리 완료.");
            return;
        }

        // Basic Path 5: 심사 요청 <<include>>
        System.out.println("\n[계약관리담당자] '심사요청' 버튼을 누릅니다.");
        System.out.println("  >> <<include>> [심사를 요청한다] 시나리오 시작");
        String uwResult = underwritingRequestSub("배서");

        // Basic Path 6,7: 심사 결과 확인
        System.out.println("\n[시스템] 심사 결과: " + uwResult);
        System.out.println("[계약관리담당자] 확인 버튼을 누릅니다.");
        enter();

        // Basic Path 8: 저장 (E1)
        System.out.println("[시스템] 배서 내용을 DB에 저장 중...");
        if (!simulateDbSave()) {
            System.out.println("[오류] 저장 실패. 관리자에게 오류를 통보합니다.");
            return;
        }
        System.out.println("[시스템] 배서 내용이 저장되고 계약정보가 갱신되었습니다.");
        System.out.println("  증권번호: " + policyNo + " | 처리일시: 2024-01-15 14:32:00");
    }
}
