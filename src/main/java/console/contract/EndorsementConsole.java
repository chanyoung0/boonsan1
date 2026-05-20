package console.contract;

import model.contract.Endorsement;
import service.contract.EndorsementService;

import static common.ConsoleUtil.*;

// 배서 관리 콘솔 I/O — 계약관리담당자 유스케이스 입출력 전담
public class EndorsementConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 배서를 관리한다");
        System.out.println("액터: 계약관리담당자");
        line();

        System.out.println("\n[Step 1] 배서 신청 정보 입력");
        input("피보험자 이름");
        input("주민등록번호");
        String policyNo = input("증권번호");
        System.out.println("  배서유형: 1. 보험가입금액 변경  2. 납입주기 변경  3. 특약 추가  4. 특약 삭제");
        System.out.print(">> 선택: ");
        String endorsementType = sc.nextLine().trim();

        System.out.println("\n[시스템] 계약정보 조회 중 (증권번호: " + policyNo + ")...");
        boolean isValidContract = rnd.nextInt(10) < 8;
        System.out.println("[시스템] 조회 결과: " + (isValidContract ? "유효 계약" : "유효하지 않은 계약 (상태: 실효)"));

        if (!isValidContract) {
            System.out.println("[시스템] \"유효하지 않은 계약입니다\" — 배서 처리를 종료합니다.");
            return;
        }

        System.out.println("[시스템] 계약정보 — 증권번호: " + policyNo + " | 계약상태: 유효");

        System.out.println("\n[계약관리담당자] 변경할 배서항목을 입력합니다.");
        String previousContent = input("변경 전 내용");
        String newContent = input("변경 내용");
        String changeReason = input("변경 사유");
        Endorsement endorsement = EndorsementService.createEndorsement(previousContent, newContent);
        System.out.println(EndorsementService.createEndorsementRequestSummary(
                policyNo, endorsementType, endorsement, changeReason));

        boolean needsReview = EndorsementService.requiresFullUnderwriting(endorsementType);
        System.out.println("\n[시스템] 심사 강도: " + (needsReview ? "정식 심사 필요 (위험 변동 배서)" : "간이 심사 (단순 조건 변경)"));

        System.out.println("\n[계약관리담당자] '심사요청' 버튼을 누릅니다.");
        System.out.println("  >> <<include>> [심사를 요청한다] 시나리오 시작");
        String uwResult;
        if (needsReview) {
            uwResult = underwritingRequestSub("배서");
        } else {
            System.out.println("  [시스템] 단순 조건 변경으로 확인되어 간이 심사를 수행합니다.");
            uwResult = "승인 (간이 심사)";
            System.out.println("  [시스템] 요청상태: '심사완료' | 결과: " + uwResult);
        }

        System.out.println("\n[시스템] 심사 결과: " + uwResult);
        enter();

        System.out.println("[시스템] 배서 내용을 DB에 저장 중...");
        String endorsementId = EndorsementService.saveEndorsement(
                policyNo, endorsementType, endorsement, changeReason, uwResult);
        if (endorsementId == null) {
            System.out.println("[오류] 저장 실패. 관리자에게 오류를 통보합니다.");
            return;
        }
        System.out.println(EndorsementService.createEndorsementSaveSummary(
                policyNo, endorsement, changeReason, uwResult));
        System.out.println("[시스템] 배서번호: " + endorsementId);
        System.out.println("[시스템] 배서 내용이 저장되고 계약정보가 갱신되었습니다. 증권번호: " + policyNo);
    }
}
