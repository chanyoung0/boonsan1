package service.contract;

import repository.ContractRepository;

import static common.ConsoleUtil.*;

// 만기계약 관리 시나리오 — 인스턴스 메서드 + 의존성 주입
public class MaturityContractService {

    private final ContractRepository contractRepository;

    // 의존성 주입으로 초기화
    public MaturityContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    // ======================================================
    // 5. 만기계약 관리
    // 액터: 계약관리담당자
    // ======================================================
    public void run() {
        line();
        System.out.println("[유스케이스] 만기계약을 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        System.out.println("\n[시스템] 만기일이 도래한 계약 목록을 자동 추출 중...");
        System.out.println("[시스템] 만기대상 계약 목록:");
        System.out.println("  No | 증권번호       | 피보험자 | 만기일       | 만기환급금");
        System.out.println("  ---|----------------|----------|--------------|----------");
        System.out.println("   1 | P2019-000123   | 박민준   | 2024-01-31   | 3,500,000원");
        System.out.println("   2 | P2019-000456   | 최수진   | 2024-01-31   | 5,200,000원");
        System.out.println("[시스템] 만기 안내장을 자동 발송 중... (SMS, 이메일)");
        System.out.println("[시스템] 발송 완료. 발송 결과가 DB에 저장되었습니다.");

        System.out.println("\n[계약관리담당자] 재계약 의사 확인 대상 목록을 조회합니다.");
        System.out.println("[시스템] 확인 대상:");
        System.out.println("  1. P2019-000123 | 박민준 | 만기: 2024-01-31 | 안내발송: 2024-01-01");
        System.out.println("  2. P2019-000456 | 최수진 | 만기: 2024-01-31 | 안내발송: 2024-01-01");

        System.out.println("\n[계약관리담당자] 재계약 의사 확인 결과를 입력합니다.");
        System.out.println("  1. 재계약 의사 있음");
        System.out.println("  2. 재계약 의사 없음");
        System.out.println("  3. 회신 없음 (기한 초과)");
        System.out.print(">> 선택: ");
        String intention = sc.nextLine().trim();

        switch (intention) {
            case "1":
                System.out.println("[시스템] 재계약 의사: '있음' 으로 저장합니다.");
                System.out.println("[시스템] 계약상태: '만기'로 갱신. 재계약 절차를 안내합니다.");
                break;
            case "2":
                System.out.println("[시스템] 만기 처리 안내:");
                System.out.println("  증권번호: P2019-000123 | 만기환급금: 3,500,000원 | 지급예정일: 2024-02-05");
                System.out.println("[계약관리담당자] 확인 버튼을 누릅니다.");
                enter();
                System.out.println("[시스템] 만기 처리 결과가 DB에 저장되고 계약상태: '만기종료'");
                break;
            case "3":
                System.out.println("[시스템] 회신 기한 초과 계약:");
                System.out.println("  P2019-000456 | 최수진 | 만기: 2024-01-31 | 안내발송: 2024-01-01");
                System.out.println("[계약관리담당자] 만기 처리 대상을 선택합니다.");
                enter();
                System.out.println("[시스템] 만기 처리 결과가 DB에 저장되고 계약상태: '만기종료'");
                break;
            default:
                System.out.println("[오류] 올바른 선택이 아닙니다.");
        }
    }
}
