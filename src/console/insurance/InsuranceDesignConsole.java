package console.insurance;

import model.insurance.Insurance;
import service.insurance.InsuranceDesignService;

import java.time.LocalDate;

import static common.ConsoleUtil.*;

// 상품 설계 콘솔 I/O — 상품개발자 유스케이스 입출력 전담
public class InsuranceDesignConsole {

    public static void run() {
        line();
        System.out.println("[유스케이스] 상품을 설계한다");
        System.out.println("액터: 상품개발자, 금융감독원");
        line();

        System.out.println("\n[상품개발자] '신규 상품 설계' 버튼을 누릅니다.");
        enter();

        System.out.println("\n[시스템] 상품 기본정보 입력 화면");
        String productName     = input("상품명");
        String productCode     = input("상품코드");
        System.out.println("  상품유형: 1. 자동차보험  2. 화재보험  3. 해상보험");
        System.out.print("  >> 선택: ");
        String typeChoice      = sc.nextLine().trim();
        input("적용대상");
        input("판매채널");
        String insurancePeriod = input("보험기간");
        input("납입기간");

        System.out.println("\n[시스템] 담보정보 입력 화면");
        input("주담보명");
        input("보장내용");
        String insuredAmountStr = input("보험가입금액 한도 (원)");
        input("면책조건");
        input("지급조건");

        System.out.println("\n[시스템] 가입조건 입력 화면");
        input("가입가능연령");
        input("가입제한조건");
        input("직업조건");
        input("건강조건");
        input("계약제한사유");

        System.out.println("\n[시스템] 보험요율 입력 및 산정 화면");
        input("기초요율 (%)");
        input("위험률 (%)");
        input("예정이율 (%)");
        input("사업비율 (%)");
        String premiumStr = input("예상 보험료 (원)");
        input("할인/할증요율 (%)");

        System.out.println("\n[상품개발자] '요율 산정' 버튼을 누릅니다.");
        enter();
        System.out.println("[시스템] 보험요율 산정 결과: 적용요율 1.2% | 예상보험료 " + premiumStr + "원 | 손익예상치 +2.3%");

        System.out.println("\n특약이 있습니까?  1. 특약 있음  2. 특약 없음");
        System.out.print(">> 선택: ");
        String specialContract = sc.nextLine().trim();
        if ("1".equals(specialContract)) {
            System.out.println("[시스템] 특약정보 입력 화면");
            input("특약명");
            input("보장내용");
            input("가입조건");
            input("특약보험료 (원)");
            input("중복가입 가능여부 (Y/N)");
        } else {
            System.out.println("[시스템] 특약정보 입력 단계를 생략합니다.");
        }

        System.out.println("\n[시스템] 상품정보 요약: " + productName + " (" + productCode + ") | 보험기간: " + insurancePeriod);
        System.out.println("[상품개발자] '저장' 버튼을 누릅니다.");
        enter();

        System.out.println("[시스템] 상품 설계 정보를 DB에 저장 중...");
        Insurance insurance = InsuranceDesignService.saveInsurance(typeChoice, productCode, insurancePeriod, insuredAmountStr, premiumStr);
        System.out.println("[시스템] 상품 상태: '설계완료' | 설계완료일시: " + LocalDate.now() + " | 상품코드: " + insurance.getProductCode());

        System.out.println("\n상품 인가를 요청하시겠습니까?  1. 예  2. 아니오 (인가 불필요)");
        System.out.print(">> 선택: ");
        String authRequest = sc.nextLine().trim();
        if (!"1".equals(authRequest)) {
            System.out.println("[시스템] \"인가가 필요 없는 상품입니다.\" — 설계 완료.");
            return;
        }

        authorizationRequestSub(productName, productCode);
    }

    private static void authorizationRequestSub(String productName, String productCode) {
        System.out.println("\n  >> <<extend>> [상품 인가를 요청한다] 시나리오 시작");
        System.out.println("  [시스템] 인가 요청 화면: " + productName + " (" + productCode + ")");
        enter();

        System.out.println("  [시스템] 인가 요청서 입력 화면");
        input("  요청일자 (YYYY-MM-DD)");
        String requestReason        = input("  요청사유");
        String submissionAgencyName = input("  제출기관명");
        input("  첨부서류 목록 (예: 상품설명서,약관,요율서)");

        System.out.println("\n  [상품개발자] '인가 요청' 버튼을 누릅니다.");
        enter();
        System.out.println("  [시스템] 인가 요청 데이터 생성 완료 | 제출 상태: '인가요청'");
        System.out.println("  [시스템] 금융감독원에 인가 요청 정보를 전송 중...");

        InsuranceDesignService.requestAuthorization(requestReason, submissionAgencyName);
        System.out.println("  [시스템] 금융감독원 전송 완료 | 인가 요청 DB 저장 완료");

        System.out.println("\n  [시스템] 금융감독원 인가 결과 대기 중...");
        enter();
        String authResult = InsuranceDesignService.simulateAuthorizationResult();
        System.out.println("  [시스템] 인가 결과: " + authResult);

        if ("보완 요청".equals(authResult)) {
            System.out.println("  [시스템] 보완 요청 사항: 약관 수정사항 | 요율 수정사항 | 첨부서류 보완");
            System.out.println("  [상품개발자] 보완 항목을 수정하고 '재제출' 버튼을 누릅니다.");
            enter();
            System.out.println("  [시스템] 재제출 처리 완료 | 최종 인가 결과: 승인");
            authResult = "승인";
        }

        System.out.println("\n  [상품개발자] '결과 확인' 버튼을 누릅니다.");
        enter();
        if ("승인".equals(authResult)) {
            System.out.println("  [시스템] 상품 상태: '인가 완료' | 인가일: " + LocalDate.now());
        } else {
            System.out.println("  [시스템] 상품 상태: '인가 불허' — 해당 상품은 판매할 수 없습니다.");
        }
    }
}
