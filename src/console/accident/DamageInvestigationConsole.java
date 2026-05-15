package console.accident;

import model.accident.DamageInvestigation;
import model.accident.InsurancePayment;
import service.accident.DamageInvestigationService;

import static common.ConsoleUtil.*;

// 손해조사 콘솔 I/O — 손해사정인/SIU 유스케이스 입출력 전담
public class DamageInvestigationConsole {

    public static void run() {
        DamageInvestigationService service = new DamageInvestigationService();

        line();
        System.out.println("[유스케이스] 손해조사를 한다");
        System.out.println("액터: 손해사정인, SIU(보험사고조사팀)");
        line();

        System.out.println("\n[손해사정인] 사고 접수 번호를 입력합니다.");
        String reportNo = input("사고 접수 번호");

        System.out.println("\n[시스템] 접수 내용:");
        System.out.println("  사고번호: " + reportNo + " | 증권번호: P2024-001234 | 사고일시: 2024-01-10 14:30");
        System.out.println("  사고경위: 교차로에서 추돌 사고 발생 | 피해내용: 차량 앞범퍼 파손, 운전자 경상");

        System.out.println("\n[손해사정인] '현장조사 자료' 버튼을 누릅니다.");
        enter();

        System.out.println("이 사고가 보험계약과 관련이 있습니까? 1. 관련 있음  2. 관련 없음 (반려)");
        System.out.print(">> 선택: ");
        String relevance = sc.nextLine().trim();

        if ("2".equals(relevance)) {
            input("사원번호");
            input("반려 사유");
            System.out.println("[시스템] 사고 접수 상태: '반려' — 반려 처리 완료.");
            return;
        }

        System.out.println("\n[시스템] 사고 관련 자료: 사고현장 사진 3장 | 블랙박스 영상 1개 | 수리 견적: 850,000원");

        System.out.println("\n보험사기가 의심됩니까? 1. 아니오  2. 예 (SIU 위임)");
        System.out.print(">> 선택: ");
        String fraud = sc.nextLine().trim();

        if ("2".equals(fraud)) {
            System.out.println("[시스템] \"보험 사기로 판단되어 조사를 요청합니다.\"");
            sc.nextLine();
            System.out.println("[시스템] 사고 접수 상태: '보험 사기 조사' — SIU에 위임됩니다.");
            return;
        }

        System.out.println("\n외부 위탁이 필요합니까? 1. 자체 조사  2. 외부 위탁");
        System.out.print(">> 선택: ");
        String outsource = sc.nextLine().trim();

        if ("2".equals(outsource)) {
            System.out.println("  >> <<extend>> [손해조사를 위탁한다] 시나리오 시작");
            outsourceInvestigation();
        }

        System.out.println("\n[손해사정인] 손해액을 입력합니다.");
        String medicalExpenseStr = input("치료비 실비 (원)");
        String lostIncomeStr     = input("휴업손해 (원)");
        String compensationStr   = input("위자료 (원)");
        String repairCostStr     = input("수리비 (원)");
        String faultRatioStr     = input("과실 비율 (%)");

        System.out.println("\n[시스템] 지급품의서 초안: 사고번호: " + reportNo + " | 예상 지급액: 980,000원");
        input("손해액 적정성 판단");
        input("과실비율 의견");
        input("특이사항 (없으면 Enter)");
        System.out.println("\n[시스템] 최종 지급품의서가 출력되었습니다.");

        String adjusterNo = input("사원번호");

        System.out.println("[시스템] 지급품의서를 DB에 저장 중...");
        DamageInvestigation investigation = service.saveInvestigation(
            adjusterNo, medicalExpenseStr, lostIncomeStr, compensationStr, repairCostStr, faultRatioStr
        );
        System.out.println("[시스템] 지급품의서 저장 완료 | 사고 접수 상태: '결재 필요'");

        System.out.println("\n  >> <<extend>> [보험금을 지급한다] 시나리오 시작");
        insurancePaymentSub(reportNo, investigation, service);
    }

    private static void outsourceInvestigation() {
        System.out.println("\n  [손해조사를 위탁한다]");
        System.out.println("  [시스템] 등록된 협력업체 목록:");
        System.out.println("    1. 삼성손해사정  2. 현대정비공장  3. 강남병원");
        System.out.print("  >> 협력업체 선택: ");
        sc.nextLine();
        input("  전송할 자료 번호 (예: 1,2,3)");
        enter();
        System.out.println("  [시스템] 협력업체에 문서 전달 완료 | 사고 조사 상태: '손해조사 위탁'");
        System.out.println("  [시스템] 위탁 조사 결과가 시스템에 반영되었습니다.");
    }

    private static void insurancePaymentSub(String reportNo, DamageInvestigation investigation, DamageInvestigationService service) {
        System.out.println("\n  [보험금을 지급한다]");
        System.out.println("  [시스템] 사고번호: " + reportNo + " | 최종 결정보험금: 980,000원");
        enter();
        System.out.println("  [시스템] 수익자 정보: 신한은행 110-123-456789 (홍길동)");
        String processorEmpNo = input("  사원번호");
        enter();
        System.out.println("  [시스템] 이체 완료: 980,000원 → 신한은행 110-123-456789 (홍길동)");

        InsurancePayment payment = service.processPayment(processorEmpNo, investigation);

        enter();
        System.out.println("  [시스템] 보험금 지급 결과 DB 저장 완료 | 사건 상태: '지급 완료'");

        boolean objected = rnd.nextInt(10) < 2;
        System.out.println("\n  [시스템] 피보험자 응답: " + (objected ? "이의 제기" : "수령 확인"));

        if (objected) {
            System.out.println("  >> <<extend>> [이의 제기를 처리한다] 시나리오 시작");
            objectionSub(service);
        }

        System.out.print("\n  제3자 과실로 구상 처리가 필요합니까? (Y/N): ");
        String subrogationAnswer = sc.nextLine().trim();
        if (service.needsSubrogation(subrogationAnswer)) {
            System.out.println("  [시스템] 사건 상태: '지급 완료/구상 처리 필요'");
        } else {
            System.out.println("  [시스템] 사건 상태: '종결'");
        }
    }

    private static void objectionSub(DamageInvestigationService service) {
        System.out.println("\n    [이의 제기를 처리한다]");
        System.out.println("    [시스템] 이의 사유: 치료비 산정 오류 | 원 지급액: 980,000원");
        System.out.println("    1. 기각  2. 수용 (재조사)  3. 법률과 이관");
        System.out.print("    >> 선택: ");
        String objResult = sc.nextLine().trim();
        String message = service.processObjection(objResult);
        System.out.println("    [시스템] " + message);
    }
}
