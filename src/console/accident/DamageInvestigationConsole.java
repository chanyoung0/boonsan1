package console.accident;

import enums.PaymentStatus;
import enums.SubrogationStatus;
import model.accident.InsurancePayment;
import model.accident.Subrogation;
import service.accident.DamageInvestigationService;
import service.accident.SubrogationService;

import static common.ConsoleUtil.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 손해조사 콘솔 I/O — 손해사정인/SIU 유스케이스 입출력 전담
public class DamageInvestigationConsole {

    public static void run() {
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
        input("치료비 실비 (원)");
        input("휴업손해 (원)");
        input("위자료 (원)");
        input("수리비 (원)");
        input("과실 비율 (%)");

        System.out.println("\n[시스템] 지급품의서 초안: 사고번호: " + reportNo + " | 예상 지급액: 980,000원");
        input("손해액 적정성 판단");
        input("과실비율 의견");
        input("특이사항 (없으면 Enter)");
        System.out.println("\n[시스템] 최종 지급품의서가 출력되었습니다.");

        input("사원번호");
        System.out.println("[시스템] 지급품의서를 DB에 저장 중...");
        if (!simulateDbSave()) {
            System.out.print("[오류] \"저장 실패\" - 다시 시도하시겠습니까? (Y/N): ");
            if (!"Y".equalsIgnoreCase(sc.nextLine().trim())) { System.out.println("[시스템] 관리자에게 오류를 통보합니다."); return; }
        }
        System.out.println("[시스템] 지급품의서 저장 완료 | 사고 접수 상태: '결재 필요'");

        System.out.println("\n  >> <<extend>> [보험금을 지급한다] 시나리오 시작");
        insurancePaymentSub(reportNo);
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

    private static void insurancePaymentSub(String reportNo) {
        System.out.println("\n  [보험금을 지급한다]");
        BigDecimal paymentAmount = BigDecimal.valueOf(980_000L);
        System.out.println("  [시스템] 사고번호: " + reportNo + " | 최종 결정보험금: 980,000원");
        enter();
        String paymentAccount = "신한은행 110-123-456789 (홍길동)";
        System.out.println("  [시스템] 수익자 정보: " + paymentAccount);
        String processorEmployeeNo = input("  사원번호");
        InsurancePayment payment = new InsurancePayment(
                "PAY-" + System.currentTimeMillis(),
                paymentAccount,
                processorEmployeeNo,
                paymentAmount,
                BigDecimal.valueOf(850_000L),
                BigDecimal.valueOf(100_000L),
                BigDecimal.valueOf(30_000L),
                BigDecimal.ZERO,
                PaymentStatus.PENDING
        );
        enter();
        PaymentStatus paymentStatus = payment.transfer();
        System.out.println("  [시스템] 이체 완료: 980,000원 → " + payment.getPaymentAccount());
        enter();
        System.out.println("  [시스템] 보험금 지급 결과 DB 저장 완료 | 지급상태: '" + paymentStatus + "' | 사건 상태: '지급 완료'");

        boolean objected = rnd.nextInt(10) < 2;
        System.out.println("\n  [시스템] 피보험자 응답: " + (objected ? "이의 제기" : "수령 확인"));

        if (objected) {
            System.out.println("  >> <<extend>> [이의 제기를 처리한다] 시나리오 시작");
            objectionSub();
        }

        String subrogationAnswer = input("  제3자 과실로 구상 처리가 필요합니까? (Y/N)");
        if (DamageInvestigationService.needsSubrogation(subrogationAnswer)) {
            System.out.println("  >> <<extend>> [구상을 처리한다] 시나리오 시작");
            subrogationSub(payment);
        } else {
            System.out.println("  [시스템] 사건 상태: '종결'");
        }
    }

    private static void subrogationSub(InsurancePayment payment) {
        System.out.println("\n    [구상을 처리한다]");
        String offenderName = input("  가해자명");
        String offenderContact = input("  가해자 연락처");
        String faultRatioInput = input("  제3자 과실비율 (%)");
        String deadlineInput = input("  납부기한 (YYYY-MM-DD, Enter: 14일 후)");
        String depositAccount = input("  구상금 입금계좌");

        float faultRatio = SubrogationService.parseFaultRatio(faultRatioInput);
        LocalDateTime paymentDeadline = SubrogationService.resolvePaymentDeadline(deadlineInput);
        Subrogation subrogation = SubrogationService.createSubrogation(
                payment,
                offenderName,
                offenderContact,
                faultRatio,
                payment.getFinalSettlementAmount(),
                paymentDeadline,
                depositAccount
        );

        System.out.println("    [시스템] 구상 접수 완료 | 구상번호: " + subrogation.getSubrogationId());
        System.out.println("    [시스템] 구상금액: " + formatAmount(subrogation.getPaymentAmount().longValue()) + " | 상태: " + subrogation.getSubrogationStatus());

        SubrogationStatus claimStatus = SubrogationService.sendClaim(subrogation);
        System.out.println("    [시스템] 구상 청구 발송 완료 | 상태: " + claimStatus);

        String depositAnswer = input("  가해자 구상금 입금이 확인되었습니까? (Y/N)");
        SubrogationStatus depositStatus = SubrogationService.confirmDeposit(subrogation, depositAnswer);
        if (depositStatus == SubrogationStatus.COMPLETED) {
            System.out.println("    [시스템] 입금 확인 완료 | 구상 상태: '" + depositStatus + "' | 사건 상태: '구상 완료'");
        } else {
            System.out.println("    [시스템] 입금 미확인 | 구상 상태: '" + depositStatus + "' | 사건 상태: '구상 진행 중'");
        }
    }

    private static void objectionSub() {
        System.out.println("\n    [이의 제기를 처리한다]");
        System.out.println("    [시스템] 이의 사유: 치료비 산정 오류 | 원 지급액: 980,000원");
        System.out.println("    1. 기각  2. 수용 (재조사)  3. 법률과 이관");
        System.out.print("    >> 선택: ");
        String objResult = sc.nextLine().trim();
        String message = DamageInvestigationService.processObjection(objResult);
        System.out.println("    [시스템] " + message);
    }
}
