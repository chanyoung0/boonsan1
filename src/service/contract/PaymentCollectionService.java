package service.contract;

import common.Notifier;
import repository.ContractRepository;

import static common.ConsoleUtil.*;

// 분납/수금 시나리오 — 인스턴스 메서드 + 의존성 주입
public class PaymentCollectionService {

    private final ContractRepository contractRepository;
    private final Notifier notifier;

    // 의존성 주입으로 초기화
    public PaymentCollectionService(ContractRepository contractRepository, Notifier notifier) {
        this.contractRepository = contractRepository;
        this.notifier = notifier;
    }

    // ======================================================
    // 4. 분납/수금 관리
    // 액터: 계약관리담당자
    // ======================================================
    public void run() {
        line();
        System.out.println("[유스케이스] 분납/수금을 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        System.out.println("\n[계약관리담당자] '수금 실행' 버튼을 누릅니다.");
        enter();

        System.out.println("[시스템] 납입기일이 도래한 수금대상 계약을 추출 중...");
        System.out.println("[시스템] 수금대상 계약 목록:");
        System.out.println("  No | 증권번호       | 피보험자 | 보험료      | 납입기일");
        System.out.println("  ---|----------------|----------|-------------|----------");
        System.out.println("   1 | P2024-001234   | 홍길동   | 150,000원   | 2024-01-15");
        System.out.println("   2 | P2024-005678   | 김영희   |  98,000원   | 2024-01-15");
        System.out.println("   3 | P2024-009012   | 이철수   | 220,000원   | 2024-01-15");
        System.out.println("  총 3건 | 합계: 468,000원");

        System.out.println("\n[계약관리담당자] '일괄처리' 버튼을 누릅니다.");
        enter();
        System.out.println("[시스템] 자동이체 일괄 실행 중...");

        boolean allSuccess = rnd.nextInt(10) < 7;
        System.out.println("[시스템] 자동이체 처리 결과: " + (allSuccess ? "전체 성공" : "일부 실패 발생"));

        if (!allSuccess) {
            System.out.println("[시스템] 처리결과: 성공 2건 / 실패 1건 / 총 수금액: 248,000원");
            System.out.println("[시스템] 자동이체 실패 계약:");
            System.out.println("  증권번호: P2024-009012 | 이철수 | 미납금액: 220,000원");
            System.out.println("[시스템] 미납 안내장 자동 발송 중...");
            System.out.println("[시스템] 미납 안내 발송 완료 (납입기한: 2024-01-31)");
            System.out.println("[시스템] P2024-009012 계약상태: '미납'으로 갱신");

            System.out.print("\n[시스템] 미납 지속 계약이 존재합니다. 이관 처리하시겠습니까? (Y/N): ");
            if ("Y".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("[시스템] 이관대상: P2024-009012 | 이철수 | 미납 3회차 / 60일");
                System.out.println("  이관 유형:");
                System.out.println("  1. 방문수금  2. 해지처리");
                System.out.print(">> 선택: ");
                String tType = sc.nextLine().trim();
                String tLabel = "2".equals(tType) ? "해지처리" : "방문수금";
                System.out.println("[시스템] 이관 처리 완료: " + tLabel);
                System.out.println("[시스템] 이관 결과가 DB에 저장되고 계약상태가 갱신됩니다.");
            }
        } else {
            System.out.println("[시스템] 처리결과: 성공 3건 / 실패 0건 / 총 수금액: 468,000원");
        }

        System.out.println("\n[계약관리담당자] 확인 버튼을 누릅니다.");
        enter();
        System.out.println("[시스템] 수금 처리 결과가 DB에 저장되고 계약정보가 갱신되었습니다.");
    }
}
