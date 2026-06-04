package com.boonsan.domain.product.service;

import com.boonsan.domain.enums.ProductStatus;
import com.boonsan.domain.model.insurance.AutoInsurance;
import com.boonsan.domain.model.insurance.FireInsurance;
import com.boonsan.domain.model.insurance.Insurance;
import com.boonsan.domain.model.insurance.MarineInsurance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.boonsan.domain.product.dto.ProductDesignRequest;
import com.boonsan.domain.product.dto.ProductResponse;
import com.boonsan.domain.product.mapper.ProductMapper;

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
        productMapper.insertProduct(insurance);
        return ProductResponse.from(insurance);
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
