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
        System.out.println("상품 유형: 1. 자동차보험  2. 화재보험  3. 해상보험");
        System.out.print(">> 선택: ");
        String productType = sc.nextLine().trim();

        String productCode = input("상품코드");
        String insurancePeriod = input("보험기간");
        BigDecimal insuredAmount = inputAmount("보험가입금액");
        BigDecimal premium = inputAmount("보험료");
        BigDecimal maturityRefund = inputAmount("만기환급금");

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

        System.out.println("[시스템] 상품 설계 완료 | " + product);
    }

    private static void requestAuthorization() {
        System.out.println("\n[유스케이스] 상품 인가를 요청한다");
        printProductList();
        String productCode = input("인가 요청 상품코드");
        String requestReason = input("인가 요청 사유");
        System.out.println("금융감독원 인가 결과: 1. 승인  2. 반려");
        System.out.print(">> 선택: ");
        String resultChoice = sc.nextLine().trim();
        boolean approved = "1".equals(resultChoice);

        Authorization authorization = InsuranceProductService.requestAuthorization(
                productCode, requestReason, approved);
        if (authorization == null) {
            System.out.println("[오류] 해당 상품코드를 찾을 수 없습니다.");
            return;
        }

        System.out.println("[시스템] 상품 인가 요청 처리 완료");
        System.out.println("  요청번호: " + authorization.getRequestId());
        System.out.println("  제출기관: " + authorization.getSubmissionAgencyName());
        System.out.println("  요청일시: " + authorization.getRequestedAt());
        System.out.println("  인가결과: " + (authorization.isApproved() ? "승인" : "반려"));
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
