package product.service;

import enums.ProductStatus;
import model.insurance.AutoInsurance;
import model.insurance.FireInsurance;
import model.insurance.Insurance;
import model.insurance.MarineInsurance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import product.dto.PremiumEstimateRequest;
import product.dto.PremiumEstimateResponse;
import product.dto.ProductDesignRequest;
import product.dto.ProductResponse;
import product.mapper.ProductMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ProductDesignApplicationService {

    private static final String INSURANCE_TYPE_AUTO = "AUTO";
    private static final String INSURANCE_TYPE_FIRE = "FIRE";
    private static final String INSURANCE_TYPE_MARINE = "MARINE";

    private final ProductMapper productMapper;

    public ProductDesignApplicationService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse create(ProductDesignRequest request) {
        Insurance insurance = toModel(request);
        applyRateFields(insurance, request);
        productMapper.insertProduct(insurance);
        return ProductResponse.from(insurance);
    }

    // 보험요율 산정 (시나리오 BP 9-10): 5개 요율 입력 → 적용요율·예상보험료·손익예상치 계산
    public PremiumEstimateResponse estimatePremium(PremiumEstimateRequest request) {
        BigDecimal insuredAmount = requirePositive(request.getInsuredAmount(), "insuredAmount");
        BigDecimal base = requireRate(request.getBaseRate(), "baseRate");
        BigDecimal risk = requireRate(request.getRiskRate(), "riskRate");
        BigDecimal interest = requireRate(request.getExpectedInterestRate(), "expectedInterestRate");
        BigDecimal expense = requireRate(request.getOperatingExpenseRatio(), "operatingExpenseRatio");
        BigDecimal discount = requireRate(request.getDiscountSurchargeRate(), "discountSurchargeRate");

        // 입력값(% 단위)을 소수로 변환
        BigDecimal baseDecimal = toDecimal(base);
        BigDecimal riskDecimal = toDecimal(risk);
        BigDecimal interestDecimal = toDecimal(interest);
        BigDecimal expenseDecimal = toDecimal(expense);
        BigDecimal discountDecimal = toDecimal(discount);

        // 순보험료율 = (기초요율 + 위험률) × (1 - 예정이율)
        BigDecimal netRate = baseDecimal.add(riskDecimal)
                .multiply(BigDecimal.ONE.subtract(interestDecimal));
        // 적용요율 = 순보험료율 × (1 + 사업비율) × (1 + 할인/할증요율)
        BigDecimal appliedRate = netRate
                .multiply(BigDecimal.ONE.add(expenseDecimal))
                .multiply(BigDecimal.ONE.add(discountDecimal))
                .setScale(6, RoundingMode.HALF_UP);
        // 예상보험료 = 보험가입금액 × 적용요율
        BigDecimal estimatedPremium = insuredAmount.multiply(appliedRate)
                .setScale(2, RoundingMode.HALF_UP);
        // 손익예상치 = 예상보험료 × (사업비율 - 위험률)
        BigDecimal profitLoss = estimatedPremium
                .multiply(expenseDecimal.subtract(riskDecimal))
                .setScale(2, RoundingMode.HALF_UP);

        return PremiumEstimateResponse.of(base, appliedRate, estimatedPremium, profitLoss);
    }

    private BigDecimal toDecimal(BigDecimal percent) {
        return percent.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal requirePositive(BigDecimal value, String fieldName) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive.");
        }
        return value;
    }

    private BigDecimal requireRate(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value;
    }

    private void applyRateFields(Insurance insurance, ProductDesignRequest request) {
        insurance.setBaseRate(request.getBaseRate());
        insurance.setRiskRate(request.getRiskRate());
        insurance.setExpectedInterestRate(request.getExpectedInterestRate());
        insurance.setOperatingExpenseRatio(request.getOperatingExpenseRatio());
        insurance.setDiscountSurchargeRate(request.getDiscountSurchargeRate());
        insurance.setAppliedRate(request.getAppliedRate());
        insurance.setProfitLossEstimate(request.getProfitLossEstimate());
    }

    @Transactional(readOnly = true)
    public ProductResponse findByProductCode(String productCode) {
        String normalizedProductCode = requireText(productCode, "productCode");
        Insurance insurance = productMapper.findByProductCode(normalizedProductCode);
        if (insurance == null) {
            throw new NoSuchElementException("Product not found: " + normalizedProductCode);
        }
        return ProductResponse.from(insurance);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        List<Insurance> products = productMapper.findAll();
        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    private Insurance toModel(ProductDesignRequest request) {
        String insuranceTypeCode = requireText(request.getInsuranceTypeCode(), "insuranceTypeCode").toUpperCase();
        String productCode = generateProductCode(insuranceTypeCode);
        LocalDateTime createdAt = LocalDateTime.now();

        return switch (insuranceTypeCode) {
            case INSURANCE_TYPE_AUTO -> new AutoInsurance(
                    productCode,
                    requireText(request.getProductName(), "productName"),
                    insuranceTypeCode,
                    normalizeOptionalText(request.getTargetCustomer()),
                    normalizeOptionalText(request.getSalesChannel()),
                    normalizeOptionalText(request.getInsurancePeriod()),
                    normalizeOptionalText(request.getPaymentPeriod()),
                    request.getInsuredAmount(),
                    request.getPremium(),
                    request.getMaturityRefund(),
                    normalizeOptionalText(request.getMainCoverage()),
                    normalizeOptionalText(request.getSubscriptionConditions()),
                    normalizeOptionalText(request.getRateInformation()),
                    normalizeOptionalText(request.getSpecialContractInfo()),
                    ProductStatus.DESIGN_COMPLETED,
                    createdAt,
                    request.getDriverAge() == null ? 0 : request.getDriverAge(),
                    normalizeOptionalText(request.getVehicleType())
            );
            case INSURANCE_TYPE_FIRE -> new FireInsurance(
                    productCode,
                    requireText(request.getProductName(), "productName"),
                    insuranceTypeCode,
                    normalizeOptionalText(request.getTargetCustomer()),
                    normalizeOptionalText(request.getSalesChannel()),
                    normalizeOptionalText(request.getInsurancePeriod()),
                    normalizeOptionalText(request.getPaymentPeriod()),
                    request.getInsuredAmount(),
                    request.getPremium(),
                    request.getMaturityRefund(),
                    normalizeOptionalText(request.getMainCoverage()),
                    normalizeOptionalText(request.getSubscriptionConditions()),
                    normalizeOptionalText(request.getRateInformation()),
                    normalizeOptionalText(request.getSpecialContractInfo()),
                    ProductStatus.DESIGN_COMPLETED,
                    createdAt,
                    normalizeOptionalText(request.getBuildingType()),
                    normalizeOptionalText(request.getLocation())
            );
            case INSURANCE_TYPE_MARINE -> new MarineInsurance(
                    productCode,
                    requireText(request.getProductName(), "productName"),
                    insuranceTypeCode,
                    normalizeOptionalText(request.getTargetCustomer()),
                    normalizeOptionalText(request.getSalesChannel()),
                    normalizeOptionalText(request.getInsurancePeriod()),
                    normalizeOptionalText(request.getPaymentPeriod()),
                    request.getInsuredAmount(),
                    request.getPremium(),
                    request.getMaturityRefund(),
                    normalizeOptionalText(request.getMainCoverage()),
                    normalizeOptionalText(request.getSubscriptionConditions()),
                    normalizeOptionalText(request.getRateInformation()),
                    normalizeOptionalText(request.getSpecialContractInfo()),
                    ProductStatus.DESIGN_COMPLETED,
                    createdAt,
                    normalizeOptionalText(request.getShippingRoute()),
                    normalizeOptionalText(request.getVesselType())
            );
            default -> throw new IllegalArgumentException("Unsupported insuranceTypeCode: " + insuranceTypeCode);
        };
    }

    private String generateProductCode(String insuranceTypeCode) {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "PRD-" + insuranceTypeCode + "-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }

    private String requireText(String value, String fieldName) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
