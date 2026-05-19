package console.compensation;

import service.compensation.PartnerManagementService;

import static common.ConsoleUtil.*;

// 협력업체 관리 콘솔 I/O — 보상담당자 유스케이스 입출력 전담
public class PartnerManagementConsole {

    public static void run() {
        PartnerManagementService service = new PartnerManagementService();

        line();
        System.out.println("[유스케이스] 협력업체를 관리한다");
        System.out.println("액터: 보상담당자");
        line();

        System.out.println("\n[보상담당자] '협력업체 관리' 메뉴를 선택합니다.");
        enter();

        System.out.println("[시스템] 협력업체 조회 조건 입력 화면");
        input("업체명 (전체: Enter)");
        input("업체유형 (병원/정비공장/법무법인/손해사정업체/전체)");
        input("계약상태 (활성/비활성/전체)");
        input("평가등급 (1~5/전체)");

        System.out.println("\n[시스템] 협력업체 목록:");
        System.out.println("  1 | 삼성손해사정     | 손해사정업체 | 활성 | EXCELLENT");
        System.out.println("  2 | 현대정비공장     | 정비공장     | 활성 | GOOD");
        System.out.println("  3 | 강남병원         | 병원         | 활성 | GOOD");

        System.out.println("\n업무를 선택하세요.  1. 기존 업체 수정  2. 신규 업체 등록");
        System.out.print(">> 선택: ");
        String action = sc.nextLine().trim();

        String partnerName, partnerType, contact, responsibility, gradeChoice;

        if ("2".equals(action)) {
            System.out.println("\n  [신규 협력업체 등록]");
            System.out.println("  [시스템] 신규 협력업체 등록 화면");
            partnerName    = input("  업체명");
            partnerType    = input("  업체유형 (병원/정비공장/법무법인/손해사정업체)");
            contact        = input("  연락처");
            responsibility = input("  담당업무");
            input("  계약조건");
            System.out.println("  평가등급: 1. EXCELLENT  2. GOOD  3. AVERAGE  4. POOR");
            System.out.print("  >> 선택: ");
            gradeChoice = sc.nextLine().trim();
        } else {
            System.out.print("\n>> 수정할 업체 번호: ");
            sc.nextLine();
            System.out.println("[시스템] 협력업체 정보 입력 화면 (기존 정보 로드 완료)");
            partnerName    = input("업체명");
            partnerType    = input("업체유형");
            contact        = input("연락처");
            responsibility = input("담당업무");
            input("계약조건");
            System.out.println("  평가등급: 1. EXCELLENT  2. GOOD  3. AVERAGE  4. POOR");
            System.out.print(">> 선택: ");
            gradeChoice = sc.nextLine().trim();
        }

        System.out.println("\n[보상담당자] '저장' 버튼을 누릅니다.");
        enter();

        System.out.println("[시스템] 협력업체 정보를 DB에 저장 중...");
        service.savePartner(partnerName, partnerType, contact, responsibility, gradeChoice);
        System.out.println("[시스템] \"저장을 완료했습니다\" | 변경 이력이 DB에 저장되었습니다.");
    }
}
