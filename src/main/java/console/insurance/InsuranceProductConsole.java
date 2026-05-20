package console.insurance;

import model.insurance.Authorization;
import model.insurance.AutoInsurance;
import model.insurance.FireInsurance;
import model.insurance.Insurance;
import model.insurance.MarineInsurance;
import service.insurance.InsuranceProductService;

import java.math.BigDecimal;
import java.util.List;

import static common.ConsoleUtil.*;

public class InsuranceProductConsole {

    public static void run() {
        while (true) {
            line();
            System.out.println("[상품 개발]");
            System.out.println("1. 상품을 설계한다");
            System.out.println("2. 상품 인가를 요청한다");
            System.out.println("3. 상품 목록 조회");
            System.out.println("4. 이전 메뉴");
            line();
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    designProduct();
                    break;
                case "2":
                    requestAuthorization();
                    break;
                case "3":
                    printProductList();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static void designProduct() {
        System.out.println("\n[유스케이스] 상품을 설계한다");
        System.out.println("[안내] 추가 설계 항목은 현재 모델에 저장 필드가 없어 요약 출력용으로만 사용됩니다.");

        System.out.println("\n[상품 기본정보 입력]");
        String productName = input("상품명");
        System.out.println("상품 유형: 1. 자동차보험  2. 화재보험  3. 해상보험");
        System.out.print(">> 선택: ");
        String productType = sc.nextLine().trim();
        String productCode = input("상품코드");
        String targetCustomer = input("적용대상");
        String salesChannel = input("판매채널");
        String insurancePeriod = input("보험기간");
        String paymentPeriod = input("납입기간");

        System.out.println("\n[담보정보 입력]");
        String mainCoverageName = input("주담보명");
        String coverageDetails = input("보장내용");
        BigDecimal insuredAmount = inputAmount("보험가입금액 한도");
        String exemptionCondition = input("면책조건");
        String paymentCondition = input("지급조건");

        System.out.println("\n[가입조건 입력]");
        String availableAge = input("가입가능연령");
        String restrictionCondition = input("가입제한조건");
        String occupationCondition = input("직업조건");
        String healthCondition = input("건강조건");

        System.out.println("\n[보험요율/손익 정보 입력]");
        String baseRate = input("기초요율");
        String riskRate = input("위험률");
        String expectedInterestRate = input("예정이율");
        String expenseRate = input("사업비율");
        String discountSurchargeRate = input("할인/할증요율");
        BigDecimal premium = inputAmount("보험료");
        BigDecimal maturityRefund = inputAmount("만기환급금");
        String expectedProfit = input("예상 손익");

        System.out.println("\n[특약정보 입력]");
        String hasSpecialContract = input("특약 있음/없음 (Y/N)");
        String specialContractDetails = "";
        if ("Y".equalsIgnoreCase(hasSpecialContract)) {
            specialContractDetails = input("특약 내용");
        } else {
            System.out.println("[시스템] 특약정보 입력 단계를 생략합니다.");
        }

        Insurance product;
        switch (productType) {
            case "1":
                int driverAge = inputInt("운전자 나이");
                String vehicleType = input("차량 유형");
                product = InsuranceProductService.designAutoInsurance(productCode, insurancePeriod,
                        insuredAmount, premium, maturityRefund, driverAge, vehicleType);
                break;
            case "2":
                String buildingType = input("건물 유형");
                String location = input("소재지");
                product = InsuranceProductService.designFireInsurance(productCode, insurancePeriod,
                        insuredAmount, premium, maturityRefund, buildingType, location);
                break;
            case "3":
                String vesselType = input("선박 유형");
                String shippingRoute = input("운항 경로");
                product = InsuranceProductService.designMarineInsurance(productCode, insurancePeriod,
                        insuredAmount, premium, maturityRefund, vesselType, shippingRoute);
                break;
            default:
                System.out.println("[오류] 지원하지 않는 상품 유형입니다.");
                return;
        }

        if (product == null) {
            System.out.println("[오류] 상품 저장에 실패했습니다. DB 연결 또는 상품코드를 확인하세요.");
            return;
        }

        System.out.println(InsuranceProductService.createDesignSummary(product, productName,
                targetCustomer, salesChannel, paymentPeriod, mainCoverageName, coverageDetails,
                exemptionCondition, paymentCondition, availableAge, restrictionCondition,
                occupationCondition, healthCondition, baseRate, riskRate, expectedInterestRate,
                expenseRate, discountSurchargeRate, expectedProfit, hasSpecialContract,
                specialContractDetails));
        System.out.println("[시스템] 상품 설계 완료 | " + product);
    }

    private static void requestAuthorization() {
        System.out.println("\n[유스케이스] 상품 인가를 요청한다");
        printProductList();
        String productCode = input("인가 요청 상품코드");
        Insurance product = InsuranceProductService.findProductByCode(productCode);
        if (product == null) {
            System.out.println("[오류] 해당 상품코드를 찾을 수 없습니다.");
            return;
        }

        System.out.println(InsuranceProductService.createAuthorizationProductDetail(product));
        String attachmentList = input("첨부서류 목록 (예: 상품설명서, 약관, 요율서, 상품개발 근거자료)");
        String requestReason = input("인가 요청 사유");
        System.out.println("[시스템] 제출기관명: 금융감독원");
        System.out.println("[시스템] 첨부서류 목록: " + (attachmentList.isEmpty() ? "입력 없음" : attachmentList));
        System.out.println("[시스템] 금융감독원 전송 예정 메시지를 생성했습니다.");
        System.out.println("[안내] 첨부서류 객체 저장과 상품 상태 저장은 현재 모델 구조상 보류됩니다.");
        System.out.println("금융감독원 인가 결과: 1. 승인  2. 반려");
        System.out.print(">> 선택: ");
        String resultChoice = sc.nextLine().trim();
        boolean approved = "1".equals(resultChoice);

        Authorization authorization = InsuranceProductService.requestAuthorization(
                productCode, requestReason, approved);
        if (authorization == null) {
            System.out.println("[오류] 상품 인가요청 저장에 실패했습니다. DB 연결 또는 상품코드를 확인하세요.");
            return;
        }

        System.out.println("[시스템] 상품 인가 요청 처리 완료");
        System.out.println("  요청번호: " + authorization.getRequestId());
        System.out.println("  제출기관: " + authorization.getSubmissionAgencyName());
        System.out.println("  요청일시: " + authorization.getRequestedAt());
        System.out.println("  인가결과: " + (authorization.isApproved() ? "승인" : "반려"));
        System.out.println("[안내] 상품 상태 저장은 현재 모델 구조상 보류됩니다.");
    }

    private static void printProductList() {
        System.out.println("\n[상품 목록 조회]");
        List<Insurance> productList = InsuranceProductService.getProductList();
        if (productList.isEmpty()) {
            System.out.println("[시스템] 등록된 상품이 없습니다.");
            return;
        }

        for (Insurance product : productList) {
            System.out.println("  상품코드: " + product.getProductCode()
                    + " | 유형: " + resolveProductType(product)
                    + " | 보험기간: " + product.getInsurancePeriod()
                    + " | 가입금액: " + formatAmount(product.getInsuredAmount().longValue())
                    + " | 보험료: " + formatAmount(product.getPremium().longValue())
                    + " | 만기환급금: " + formatAmount(product.getMaturityRefund().longValue()));
        }
    }

    private static String resolveProductType(Insurance product) {
        if (product instanceof AutoInsurance) {
            return "자동차보험";
        }
        if (product instanceof FireInsurance) {
            return "화재보험";
        }
        if (product instanceof MarineInsurance) {
            return "해상보험";
        }
        return "보험상품";
    }

    private static BigDecimal inputAmount(String label) {
        while (true) {
            String value = input(label + " (원)").replace(",", "").trim();
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.println("[오류] 숫자로 입력하세요.");
            }
        }
    }

    private static int inputInt(String label) {
        while (true) {
            String value = input(label).trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("[오류] 숫자로 입력하세요.");
            }
        }
    }
}
