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
}
