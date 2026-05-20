package console.accident;

import enums.AcceptanceStatus;
import enums.PaymentStatus;
import enums.RequestStatus;
import model.accident.DamageInvestigation;
import enums.SubrogationStatus;
import model.accident.InsurancePayment;
import model.accident.Objection;
import model.accident.OutsourceRequest;
import model.accident.Subrogation;
import model.partner.Partner;
import service.accident.DamageInvestigationService;
import service.accident.SubrogationService;
import service.partner.PartnerService;

import static common.ConsoleUtil.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 손해조사 콘솔 I/O — 손해사정인/SIU 유스케이스 입출력 전담
public class DamageInvestigationConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 손해조사를 한다");
        System.out.println("액터: 손해사정인, SIU(보험사고조사팀)");
        line();

        System.out.println("\n[손해사정인] 사고 접수 번호를 입력합니다.");
        String reportNo = input("사고 접수 번호");
        DamageInvestigation investigation = new DamageInvestigation();
        investigation.setInvestigationId("INV-" + System.currentTimeMillis());
        investigation.setInvestigationAt(LocalDateTime.now());

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
            investigation.rejectClaim();
            System.out.println("[시스템] 사고 접수 상태: '반려' — 반려 처리 완료.");
            return;
        }

        System.out.println("\n[시스템] 사고 관련 자료: 사고현장 사진 3장 | 블랙박스 영상 1개 | 수리 견적: 850,000원");

        System.out.println("\n보험사기가 의심됩니까? 1. 아니오  2. 예 (SIU 위임)");
        System.out.print(">> 선택: ");
        String fraud = sc.nextLine().trim();

        if ("2".equals(fraud)) {
            RequestStatus fraudRequestStatus = investigation.requestFraudInvestigation();
            System.out.println("[시스템] \"보험 사기로 판단되어 조사를 요청합니다.\"");
            enter();
            System.out.println("[시스템] 사고 접수 상태: '보험 사기 조사' — SIU에 위임됩니다. | 요청상태: " + fraudRequestStatus);
            return;
        }

        System.out.println("\n외부 위탁이 필요합니까? 1. 자체 조사  2. 외부 위탁");
        System.out.print(">> 선택: ");
        String outsource = sc.nextLine().trim();

        if ("2".equals(outsource)) {
            System.out.println("  >> <<extend>> [손해조사를 위탁한다] 시나리오 시작");
            OutsourceRequest outsourceRequest = outsourceInvestigation();
            if (outsourceRequest != null) {
                investigation.delegateInvestigation();
                investigation.setOutsourceRequest(outsourceRequest);
            }
        }

        System.out.println("\n[손해사정인] 손해액을 입력합니다.");
        BigDecimal medicalExpense = DamageInvestigationService.parseAmount(input("치료비 실비 (원)"));
        BigDecimal lostIncome = DamageInvestigationService.parseAmount(input("휴업손해 (원)"));
        BigDecimal consolationMoney = DamageInvestigationService.parseAmount(input("위자료 (원)"));
        BigDecimal repairCost = DamageInvestigationService.parseAmount(input("수리비 (원)"));
        float faultRatio = SubrogationService.parseFaultRatio(input("과실 비율 (%)"));
        BigDecimal paymentAmount = DamageInvestigationService.sumAmounts(medicalExpense, lostIncome, consolationMoney, repairCost);
        investigation.setMedicalExpense(medicalExpense);
        investigation.setLostIncome(lostIncome);
        investigation.setRepairCost(repairCost);
        investigation.setFaultRatio(faultRatio);
        investigation.setSettlementAmount(paymentAmount);

        System.out.println("\n[시스템] 지급품의서 초안: 사고번호: " + reportNo + " | 예상 지급액: " + formatAmount(paymentAmount.longValue()));
        input("손해액 적정성 판단");
        input("과실비율 의견");
        input("특이사항 (없으면 Enter)");
        System.out.println("\n[시스템] 최종 지급품의서가 출력되었습니다.");

        String adjusterId = input("사원번호");
        investigation.setAdjusterId(adjusterId);
        System.out.println("[시스템] 지급품의서를 DB에 저장 중...");
        String investigationId = DamageInvestigationService.saveDamageInvestigation(investigation, reportNo);
        if (investigationId == null) {
            System.out.println("[오류] 저장 실패. 관리자에게 오류를 통보합니다.");
            return;
        }
        System.out.println("[시스템] 지급품의서 저장 완료 | 조사번호: " + investigationId + " | 사고 접수 상태: '결재 필요'");

        String paymentAvailable = input("보험금 지급이 가능합니까? (Y/N)");
        if (!DamageInvestigationService.isYes(paymentAvailable)) {
            investigation.rejectClaim();
            System.out.println("[시스템] 보험금 지급 불가로 반려 처리합니다.");
            return;
        }

        System.out.println("\n  >> <<extend>> [보험금을 지급한다] 시나리오 시작");
        insurancePaymentSub(reportNo, investigation, paymentAmount, repairCost, medicalExpense, lostIncome);
    }

    private static OutsourceRequest outsourceInvestigation() {
        System.out.println("\n  [손해조사를 위탁한다]");
        List<Partner> partnerList = PartnerService.getAvailablePartnerList();
        if (partnerList.isEmpty()) {
            System.out.println("  [시스템] 사용 가능한 협력업체가 없습니다. 협력업체 관리 메뉴에서 등록 후 다시 진행해야 합니다.");
            return null;
        }

        System.out.println("  [시스템] 등록된 협력업체 목록:");
        for (int i = 0; i < partnerList.size(); i++) {
            Partner partner = partnerList.get(i);
            System.out.println("    " + (i + 1) + ". " + partner.getPartnerName()
                    + " | 유형: " + partner.getPartnerType()
                    + " | 담당업무: " + partner.getResponsibility()
                    + " | 평가등급: " + partner.getEvaluationGrade());
        }
        System.out.print("  >> 협력업체 선택: ");
        String selectedPartnerNo = sc.nextLine().trim();
        Partner selectedPartner = selectPartner(partnerList, selectedPartnerNo);
        if (selectedPartner == null) {
            System.out.println("  [시스템] 선택한 협력업체를 찾을 수 없습니다. 위탁 흐름을 종료합니다.");
            return null;
        }

        String transferredDataInput = input("  전송할 자료 번호 (예: 1,2,3)");
        OutsourceRequest outsourceRequest = new OutsourceRequest(
                "OUT-" + System.currentTimeMillis(),
                RequestStatus.PENDING,
                LocalDateTime.now(),
                selectedPartner
        );
        outsourceRequest.setTransferredDataList(splitTransferredData(transferredDataInput));
        outsourceRequest.selectPartner();
        outsourceRequest.selectDataToTransfer();
        outsourceRequest.send();
        outsourceRequest.setRequestStatus(RequestStatus.COMPLETED);
        outsourceRequest.setResult("위탁 조사 결과 반영");
        outsourceRequest.receiveResult();
        enter();
        System.out.println("  [시스템] 협력업체에 문서 전달 완료 | 사고 조사 상태: '손해조사 위탁'");
        System.out.println("  [시스템] 위탁요청번호: " + outsourceRequest.getRequestId()
                + " | 협력업체: " + selectedPartner.getPartnerName()
                + " | 요청상태: " + outsourceRequest.getRequestStatus());
        System.out.println("  [시스템] 위탁 조사 결과가 시스템에 반영되었습니다.");
        return outsourceRequest;
    }

    private static void insurancePaymentSub(String reportNo, DamageInvestigation investigation,
                                            BigDecimal paymentAmount, BigDecimal repairCost,
                                            BigDecimal medicalExpense, BigDecimal lostIncome) {
        System.out.println("\n  [보험금을 지급한다]");
        System.out.println("  [시스템] 사고번호: " + reportNo + " | 최종 결정보험금: " + formatAmount(paymentAmount.longValue()));
        enter();
        String paymentAccount = "신한은행 110-123-456789 (홍길동)";
        System.out.println("  [시스템] 수익자 정보: " + paymentAccount);
        String processorEmployeeNo = input("  사원번호");
        InsurancePayment payment = new InsurancePayment(
                "PAY-" + System.currentTimeMillis(),
                paymentAccount,
                processorEmployeeNo,
                paymentAmount,
                repairCost,
                medicalExpense,
                lostIncome,
                BigDecimal.ZERO,
                PaymentStatus.PENDING
        );
        if (investigation != null) {
            investigation.setInsurancePayment(payment);
        }
        enter();
        if (payment.generatePaymentRecord() == null) {
            System.out.println("  [시스템] 지급기록 객체는 현재 모델 연결이 없어 지급 요약 출력으로 대체합니다.");
        }
        PaymentStatus paymentStatus = payment.transfer();
        System.out.println("  [시스템] 이체 완료: " + formatAmount(paymentAmount.longValue()) + " → " + payment.getPaymentAccount());
        enter();
        payment.sendNotification();
        System.out.println("  [시스템] 보험금 지급 결과 DB 저장 완료 | 지급상태: '" + paymentStatus + "' | 사건 상태: '지급 완료'");

        String response = input("  피보험자 응답을 선택하세요(1. 수령 확인  2. 이의 제기)");
        boolean objected = "2".equals(response);
        System.out.println("\n  [시스템] 피보험자 응답: " + (objected ? "이의 제기" : "수령 확인"));

        if (objected) {
            System.out.println("  >> <<extend>> [이의 제기를 처리한다] 시나리오 시작");
            objectionSub();
        } else {
            payment.confirmReceipt();
        }

        String subrogationAnswer = input("  제3자 과실로 구상 처리가 필요합니까? (Y/N)");
        if (DamageInvestigationService.needsSubrogation(subrogationAnswer)) {
            System.out.println("  >> <<extend>> [구상을 처리한다] 시나리오 시작");
            subrogationSub(payment);
        } else {
            payment.closeCase();
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
        System.out.println("    [시스템] 구상 문서 요약: " + subrogation.generateSubrogationDocument());

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

    private static Partner selectPartner(List<Partner> partnerList, String selectedPartnerNo) {
        try {
            int index = Integer.parseInt(selectedPartnerNo) - 1;
            if (index < 0 || index >= partnerList.size()) {
                return null;
            }
            return partnerList.get(index);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static List<String> splitTransferredData(String transferredDataInput) {
        List<String> transferredDataList = new ArrayList<>();
        if (transferredDataInput == null || transferredDataInput.trim().isEmpty()) {
            return transferredDataList;
        }

        String[] tokens = transferredDataInput.split(",");
        for (String token : tokens) {
            if (!token.trim().isEmpty()) {
                transferredDataList.add(token.trim());
            }
        }
        return transferredDataList;
    }

    private static void objectionSub() {
        System.out.println("\n    [이의 제기를 처리한다]");
        System.out.println("    [시스템] 이의 사유: 치료비 산정 오류 | 원 지급액: 980,000원");
        Objection objection = new Objection(
                "OBJ-" + System.currentTimeMillis(),
                "홍길동",
                "치료비 산정 오류",
                "원 지급액: 980,000원",
                AcceptanceStatus.PENDING
        );
        System.out.println("    1. 기각  2. 수용 (재조사)  3. 법률과 이관");
        System.out.print("    >> 선택: ");
        String objResult = sc.nextLine().trim();
        if ("2".equals(objResult)) {
            objection.setAdjustedAmount(DamageInvestigationService.parseAmount(input("    조정 지급액 (없으면 Enter)")));
            AcceptanceStatus status = objection.acceptObjection();
            System.out.println("    [시스템] 이의제기 객체 처리상태: " + status + " | 이의번호: " + objection.getObjectionId());
        } else if ("3".equals(objResult)) {
            objection.setTransferReason(input("    법률과 이관 사유"));
            objection.transferToLegal();
            objection.setAcceptanceStatus(AcceptanceStatus.TRANSFERRED_TO_LEGAL);
            objection.receiveLegalResult();
            System.out.println("    [시스템] 이의제기 객체 처리상태: " + objection.getAcceptanceStatus() + " | 이의번호: " + objection.getObjectionId());
        } else {
            AcceptanceStatus status = objection.rejectObjection();
            System.out.println("    [시스템] 이의제기 객체 처리상태: " + status + " | 이의번호: " + objection.getObjectionId());
        }
        String message = DamageInvestigationService.processObjection(objResult);
        System.out.println("    [시스템] " + message);
    }
}
