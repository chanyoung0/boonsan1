package com.boonsan.service.accident;

import static com.boonsan.common.ConsoleUtil.*;

public class DamageInvestigationService {

    // ======================================================
    // 7. 손해조사
    // 액터: 손해사정인, SIU
    // ======================================================
    public static void run() {
        line();
        System.out.println("[유스케이스] 손해조사를 한다");
        System.out.println("액터: 손해사정인, SIU(보험사고조사팀)");
        line();

        // Basic Path 1~2: 사고 접수 조회
        System.out.println("\n[손해사정인] 사고 접수 목록에서 사고 접수 번호를 입력합니다.");
        String reportNo = input("사고 접수 번호");

        System.out.println("\n[시스템] 접수 내용:");
        System.out.println("  사고번호: " + reportNo);
        System.out.println("  증권번호: P2024-001234 | 사고일시: 2024-01-10 14:30");
        System.out.println("  사고경위: 교차로에서 추돌 사고 발생");
        System.out.println("  피해내용: 차량 앞범퍼 파손, 운전자 경상");

        // Basic Path 3: 현장조사 자료 — 손해사정인 판단 (A1 분기)
        System.out.println("\n[손해사정인] '현장조사 자료' 버튼을 누릅니다.");
        enter();

        System.out.println("이 사고가 보험계약과 관련이 있습니까?");
        System.out.println("  1. 관련 있음  2. 관련 없음 (반려)");
        System.out.print(">> 선택: ");
        String relevance = sc.nextLine().trim();

        if ("2".equals(relevance)) {
            // A1: 반려
            System.out.println("[손해사정인] '보험 처리 반려' 버튼을 누릅니다.");
            input("사원번호");
            input("반려 사유");
            System.out.println("[시스템] 사고 접수 상태: '반려'");
            System.out.println("[시스템] 반려 처리 완료.");
            return;
        }

        // Basic Path 4: 현장자료 출력
        System.out.println("\n[시스템] 사고 관련 자료:");
        System.out.println("  사고현장 사진 3장 | 블랙박스 영상 1개 | 수리 견적: 850,000원");

        // A2: 보험사기 의심 — 손해사정인 판단
        System.out.println("\n보험사기가 의심됩니까?");
        System.out.println("  1. 아니오  2. 예 (SIU 위임)");
        System.out.print(">> 선택: ");
        String fraud = sc.nextLine().trim();

        if ("2".equals(fraud)) {
            // A2: 보험사기 의심
            System.out.println("[손해사정인] '보험 사기 평가' 버튼을 누릅니다.");
            System.out.println("[시스템] \"보험 사기로 판단되어 조사를 요청합니다.\"");
            System.out.print("[손해사정인] 계속하려면 '실시한다'를 입력하세요: ");
            sc.nextLine();
            System.out.println("[시스템] 사고 접수 상태: '보험 사기 조사'");
            System.out.println("[시스템] SIU(보험사기조사팀)에 사건이 위임됩니다.");
            return;
        }

        // Basic Path 5: 손해액 산정 — 손해사정인 판단 (A3 분기)
        System.out.println("\n외부 위탁이 필요합니까?");
        System.out.println("  1. 자체 조사  2. 외부 위탁");
        System.out.print(">> 선택: ");
        String outsource = sc.nextLine().trim();

        if ("2".equals(outsource)) {
            // A3: 손해조사 위탁 <<extend>>
            System.out.println("[손해사정인] '손해조사를 위탁한다' 버튼을 누릅니다.");
            System.out.println("  >> <<extend>> [손해조사를 위탁한다] 시나리오 시작");
            outsourceInvestigation();
        }

        System.out.println("\n[손해사정인] 손해액을 입력합니다.");
        input("치료비 실비 (원)");
        input("휴업손해 (원)");
        input("위자료 (원)");
        input("수리비 (원)");
        input("과실 비율 (%)");

        // Basic Path 6~8: 지급품의서 작성
        System.out.println("\n[시스템] 지급품의서 초안:");
        System.out.println("  사고번호: " + reportNo);
        System.out.println("  치료비: 350,000원 | 수리비: 850,000원 | 위자료: 200,000원");
        System.out.println("  예상 지급액: 980,000원");

        System.out.println("\n[손해사정인] 소견을 작성합니다.");
        input("손해액 적정성 판단");
        input("과실비율 의견");
        input("특이사항 (없으면 Enter)");

        System.out.println("\n[시스템] 최종 지급품의서가 출력되었습니다.");

        // Basic Path 9~10: 결재 및 저장 (E1)
        System.out.println("[손해사정인] '결재' 버튼을 누릅니다.");
        input("사원번호");

        System.out.println("[시스템] 지급품의서를 DB에 저장 중...");
        if (!simulateDbSave()) {
            System.out.print("[오류] \"저장 실패\" - 다시 시도하시겠습니까? (Y/N): ");
            if (!"Y".equalsIgnoreCase(sc.nextLine().trim())) {
                System.out.println("[시스템] 관리자에게 오류를 통보합니다.");
                return;
            }
        }
        System.out.println("[시스템] 지급품의서가 저장되었습니다.");
        System.out.println("[시스템] 사고 접수 상태: '결재 필요'");

        // Basic Path 11: 보험금 지급 <<extend>>
        System.out.println("\n  >> <<extend>> [보험금을 지급한다] 시나리오 시작");
        insurancePaymentSub(reportNo);
    }

    private static void outsourceInvestigation() {
        System.out.println("\n  [손해조사를 위탁한다]");
        System.out.println("  [시스템] 등록된 협력업체 목록:");
        System.out.println("    1. 삼성손해사정 (손해사정 전문)");
        System.out.println("    2. 현대정비공장 (차량 수리 전문)");
        System.out.println("    3. 강남병원 (의료 심사)");
        System.out.print("  >> 협력업체 선택: ");
        sc.nextLine();
        System.out.println("  [시스템] 전송할 자료를 선택하세요 (체크리스트):");
        System.out.println("    1. 보험계약 내용  2. 사고경위서  3. 현장사진  4. 블랙박스 영상");
        input("  전송할 자료 번호 (예: 1,2,3)");
        System.out.println("  [시스템] 손해사정 위탁 의뢰서를 문서로 정리합니다...");
        System.out.println("  [손해사정인] '제출한다'를 클릭합니다.");
        enter();
        System.out.println("  [시스템] 협력업체에 문서 전달 완료.");
        System.out.println("  [시스템] 사고 조사 상태: '손해조사 위탁'");
        System.out.println("  [시스템] 위탁 조사 결과가 도착했습니다. (알림)");
        System.out.println("  [시스템] 위탁 조사 결과가 시스템에 반영되었습니다.");
    }

    private static void insurancePaymentSub(String reportNo) {
        System.out.println("\n  [보험금을 지급한다]");
        System.out.println("  액터: 보상담당자, 피보험자");

        System.out.println("  [보상담당자] '결재 완료' 상태의 사건을 선택합니다.");
        System.out.println("  [시스템] 지급 정보 (문서 출력):");
        System.out.println("    사고번호: " + reportNo);
        System.out.println("    치료비: 350,000원 | 수리비: 850,000원 | 위자료: 200,000원");
        System.out.println("    최종 결정보험금: 980,000원 | 자사 보유금 예상: 20,000,000원");

        System.out.println("  [보상담당자] '다음' 버튼을 누릅니다.");
        enter();

        System.out.println("  [시스템] 수익자 정보:");
        System.out.println("    계좌번호: 110-123-456789 | 은행: 신한은행 | 예금주: 홍길동");

        input("  [보상담당자] 사원번호 입력");
        System.out.println("  [보상담당자] '최종 이체 및 종결' 버튼을 누릅니다.");
        enter();

        // E1: 이체 처리
        System.out.println("  [시스템] 계좌이체 처리 중...");
        System.out.println("  [시스템] 이체 완료: 980,000원 → 신한은행 110-123-456789 (홍길동)");
        System.out.println("  [보상담당자] '다음' 버튼을 누릅니다.");
        enter();

        // E2: 지급 결과 저장
        System.out.println("  [시스템] 보험금 지급 결과를 DB에 저장 중...");
        System.out.println("  [시스템] 사고 접수자에게 알림 발송 완료.");
        System.out.println("  [시스템] 사건 상태: '지급 완료'");

        // 시스템 자동: 피보험자 응답 시뮬레이션 (80% 수령 확인, 20% 이의 제기)
        boolean objected = rnd.nextInt(10) < 2;
        System.out.println("\n  [시스템] 피보험자 응답 수신: " + (objected ? "이의 제기" : "수령 확인 (이의 없음)"));

        if (objected) {
            // A1: 이의 제기 <<extend>>
            System.out.println("  [피보험자] 지급 금액에 이의를 제기합니다.");
            System.out.println("  >> <<extend>> [이의 제기를 처리한다] 시나리오 시작");
            objectionSub();
        } else {
            System.out.println("  [시스템] 피보험자 수령 확인 완료.");
        }

        // Basic Path 11: 종결 or A2(구상) — 보상담당자 판단
        System.out.println("\n  [보상담당자] 알림을 확인합니다.");
        System.out.print("  제3자 과실로 구상 처리가 필요합니까? (Y/N): ");
        String subroga = sc.nextLine().trim();

        if ("Y".equalsIgnoreCase(subroga)) {
            // A2: 구상 대기
            System.out.println("  [보상담당자] '구상 대기 등록' 버튼을 누릅니다.");
            System.out.println("  [시스템] 사건 상태: '지급 완료/구상 처리 필요'");
        } else {
            System.out.println("  [보상담당자] '사건 종결' 버튼을 누릅니다.");
            System.out.println("  [시스템] 사건 상태: '종결'");
        }
    }

    private static void objectionSub() {
        System.out.println("\n    [이의 제기를 처리한다]");
        System.out.println("    [시스템] 이의 제기 내용:");
        System.out.println("      이의 사유: 치료비 산정 오류 | 원 지급액: 980,000원");
        System.out.println("    [보상담당자] 재검토 의견을 작성합니다.");
        System.out.println("    처리 방법:");
        System.out.println("      1. 기각  2. 수용 (재조사)  3. 법률과 이관");
        System.out.print("    >> 선택: ");
        String objResult = sc.nextLine().trim();

        switch (objResult) {
            case "1":
                System.out.println("    [시스템] 기각 사유서를 작성하여 이의 제기자에게 발송합니다.");
                System.out.println("    [시스템] 기각 사유서가 DB에 저장되었습니다.");
                break;
            case "2":
                System.out.println("    [시스템] 이의 제기 수용 - 재조사 필요.");
                System.out.println("    [시스템] 사건 상태: '재조사 필요'");
                System.out.println("    [시스템] 손해조사를 다시 진행합니다.");
                break;
            case "3":
                System.out.println("    [보상담당자] '법률과 이관' 버튼을 누릅니다.");
                input("    이관 사유");
                System.out.println("    [시스템] 관련 서류를 법률과에 전달합니다.");
                System.out.println("    [시스템] 사건 상태: '법률과 이관'");
                System.out.println("    [시스템] 법률과 처리 완료 시 결과를 보상담당자에게 알림.");
                break;
            default:
                System.out.println("    [시스템] 기각 처리합니다.");
        }
    }
}
