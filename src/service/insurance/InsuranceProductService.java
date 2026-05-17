package service.insurance;

import model.insurance.Authorization;
import model.insurance.AutoInsurance;
import model.insurance.FinancialSupervisoryService;
import model.insurance.FireInsurance;
import model.insurance.Insurance;
import model.insurance.MarineInsurance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InsuranceProductService {

    private static final List<Insurance> productList = new ArrayList<>();
    private static final List<Authorization> authorizationList = new ArrayList<>();

    public static AutoInsurance designAutoInsurance(String productCode, String insurancePeriod,
                                                    BigDecimal insuredAmount, BigDecimal premium,
                                                    BigDecimal maturityRefund, int driverAge,
                                                    String vehicleType) {
        AutoInsurance product = new AutoInsurance(productCode, insurancePeriod, insuredAmount,
                premium, maturityRefund, driverAge, vehicleType);
        registerProduct(product);
        return product;
    }

    public static FireInsurance designFireInsurance(String productCode, String insurancePeriod,
                                                    BigDecimal insuredAmount, BigDecimal premium,
                                                    BigDecimal maturityRefund, String buildingType,
                                                    String location) {
        FireInsurance product = new FireInsurance(productCode, insurancePeriod, insuredAmount,
                premium, maturityRefund, buildingType, location);
        registerProduct(product);
        return product;
    }

    public static MarineInsurance designMarineInsurance(String productCode, String insurancePeriod,
                                                        BigDecimal insuredAmount, BigDecimal premium,
                                                        BigDecimal maturityRefund, String vesselType,
                                                        String shippingRoute) {
        MarineInsurance product = new MarineInsurance(productCode, insurancePeriod, insuredAmount,
                premium, maturityRefund, vesselType, shippingRoute);
        registerProduct(product);
        return product;
    }

    public static List<Insurance> getProductList() {
        return new ArrayList<>(productList);
    }

    public static String createDesignSummary(Insurance product, String productName,
                                             String targetCustomer, String salesChannel,
                                             String paymentPeriod, String mainCoverageName,
                                             String coverageDetails, String exemptionCondition,
                                             String paymentCondition, String availableAge,
                                             String restrictionCondition, String occupationCondition,
                                             String healthCondition, String baseRate,
                                             String riskRate, String expectedInterestRate,
                                             String expenseRate, String discountSurchargeRate,
                                             String expectedProfit, String hasSpecialContract,
                                             String specialContractDetails) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n[시스템] 상품 설계 요약\n");
        builder.append("  상품명: ").append(emptyToDefault(productName)).append("\n");
        builder.append("  상품코드: ").append(product.getProductCode()).append("\n");
        builder.append("  상품유형: ").append(resolveProductType(product)).append("\n");
        builder.append("  적용대상: ").append(emptyToDefault(targetCustomer)).append("\n");
        builder.append("  판매채널: ").append(emptyToDefault(salesChannel)).append("\n");
        builder.append("  보험기간: ").append(emptyToDefault(product.getInsurancePeriod())).append("\n");
        builder.append("  납입기간: ").append(emptyToDefault(paymentPeriod)).append("\n");
        builder.append("  주담보명: ").append(emptyToDefault(mainCoverageName)).append("\n");
        builder.append("  보장내용: ").append(emptyToDefault(coverageDetails)).append("\n");
        builder.append("  보험가입금액 한도: ").append(formatAmount(product.getInsuredAmount())).append("\n");
        builder.append("  면책조건: ").append(emptyToDefault(exemptionCondition)).append("\n");
        builder.append("  지급조건: ").append(emptyToDefault(paymentCondition)).append("\n");
        builder.append("  가입가능연령: ").append(emptyToDefault(availableAge)).append("\n");
        builder.append("  가입제한조건: ").append(emptyToDefault(restrictionCondition)).append("\n");
        builder.append("  직업조건: ").append(emptyToDefault(occupationCondition)).append("\n");
        builder.append("  건강조건: ").append(emptyToDefault(healthCondition)).append("\n");
        builder.append("  기초요율: ").append(emptyToDefault(baseRate)).append("\n");
        builder.append("  위험률: ").append(emptyToDefault(riskRate)).append("\n");
        builder.append("  예정이율: ").append(emptyToDefault(expectedInterestRate)).append("\n");
        builder.append("  사업비율: ").append(emptyToDefault(expenseRate)).append("\n");
        builder.append("  할인/할증요율: ").append(emptyToDefault(discountSurchargeRate)).append("\n");
        builder.append("  보험료: ").append(formatAmount(product.getPremium())).append("\n");
        builder.append("  만기환급금: ").append(formatAmount(product.getMaturityRefund())).append("\n");
        builder.append("  예상 손익: ").append(emptyToDefault(expectedProfit)).append("\n");
        builder.append("  특약 여부: ").append(emptyToDefault(hasSpecialContract)).append("\n");
        builder.append("  특약 내용: ").append(emptyToDefault(specialContractDetails)).append("\n");
        builder.append("  안내: 현재 모델에 저장 필드가 없는 항목은 요약 출력용으로만 사용됩니다.");
        return builder.toString();
    }

    public static String createAuthorizationProductDetail(Insurance product) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n[시스템] 선택 상품 인가 요청 상세 정보\n");
        builder.append("  상품코드: ").append(product.getProductCode()).append("\n");
        builder.append("  상품유형: ").append(resolveProductType(product)).append("\n");
        builder.append("  보험기간: ").append(emptyToDefault(product.getInsurancePeriod())).append("\n");
        builder.append("  보험가입금액: ").append(formatAmount(product.getInsuredAmount())).append("\n");
        builder.append("  보험료: ").append(formatAmount(product.getPremium())).append("\n");
        builder.append("  만기환급금: ").append(formatAmount(product.getMaturityRefund())).append("\n");
        builder.append("  보장내용/가입조건/보험요율/특약정보: 현재 모델 저장 필드 없음");
        return builder.toString();
    }

    public static Insurance findProductByCode(String productCode) {
        for (Insurance product : productList) {
            if (product.getProductCode().equals(productCode)) {
                return product;
            }
        }
        return null;
    }

    public static Authorization requestAuthorization(String productCode, String requestReason,
                                                     boolean approved) {
        Insurance product = findProductByCode(productCode);
        if (product == null) {
            return null;
        }

        FinancialSupervisoryService supervisoryService =
                new FinancialSupervisoryService("FSS", "금융감독원");
        supervisoryService.receiveAuthorizationRequest();

        Authorization authorization = new Authorization(
                "AUTH-" + System.currentTimeMillis(),
                requestReason,
                supervisoryService.getInstitutionName(),
                LocalDateTime.now(),
                supervisoryService
        );
        authorization.sendAuthorizationRequest();
        authorization.setApproved(approved);
        if (approved) {
            authorization.setApprovedAt(LocalDateTime.now());
            product.changeProductStatus();
        }
        supervisoryService.sendAuthorizationResult();
        authorization.applyAuthorizationResult();
        authorization.updateProductStatus();
        authorizationList.add(authorization);
        return authorization;
    }

    public static List<Authorization> getAuthorizationList() {
        return new ArrayList<>(authorizationList);
    }

    private static void registerProduct(Insurance product) {
        product.saveProductInfo();
        productList.add(product);
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

    private static String formatAmount(BigDecimal amount) {
        return amount == null ? "미입력" : amount.toPlainString() + "원";
    }

    private static String emptyToDefault(String value) {
        return value == null || value.trim().isEmpty() ? "미입력" : value;
    }
}
