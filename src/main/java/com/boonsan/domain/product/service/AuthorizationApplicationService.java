package com.boonsan.domain.product.service;

import com.boonsan.domain.enums.AuthorizationStatus;
import com.boonsan.domain.enums.ProductStatus;
import com.boonsan.domain.model.insurance.Authorization;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.boonsan.domain.product.dto.AuthorizationCreateRequest;
import com.boonsan.domain.product.dto.AuthorizationEligibilityResponse;
import com.boonsan.domain.product.dto.AuthorizationResponse;
import com.boonsan.domain.product.dto.AuthorizationRevisionRequest;
import com.boonsan.domain.product.mapper.AuthorizationMapper;
import com.boonsan.domain.product.mapper.ProductMapper;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthorizationApplicationService {

    private final AuthorizationMapper authorizationMapper;
    private final ProductMapper productMapper;

    public AuthorizationApplicationService(AuthorizationMapper authorizationMapper, ProductMapper productMapper) {
        this.authorizationMapper = authorizationMapper;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public AuthorizationEligibilityResponse getEligibility(String productCode) {
        String normalizedProductCode = requireText(productCode, "productCode");
        AuthorizationEligibilityResponse response =
                authorizationMapper.findEligibilityByProductCode(normalizedProductCode);

        if (response == null) {
            AuthorizationEligibilityResponse notFound = new AuthorizationEligibilityResponse();
            notFound.setProductCode(normalizedProductCode);
            notFound.setEligible(false);
            notFound.setMessage("Product not found: " + normalizedProductCode);
            return notFound;
        }

        ProductStatus productStatus = response.getProductStatus();
        boolean eligible = productStatus == ProductStatus.DESIGN_COMPLETED
                || productStatus == ProductStatus.REVISION_REQUESTED;
        response.setEligible(eligible);
        response.setMessage(buildEligibilityMessage(productStatus, eligible));
        return response;
    }

    @Transactional(readOnly = true)
    public AuthorizationResponse getLatest(String productCode) {
        String normalizedProductCode = requireText(productCode, "productCode");
        Authorization authorization = authorizationMapper.findLatestByProductCode(normalizedProductCode);
        if (authorization == null) {
            throw new NoSuchElementException("Authorization not found for product: " + normalizedProductCode);
        }
        ProductStatus productStatus = loadProductStatus(normalizedProductCode);
        return AuthorizationResponse.from(authorization, productStatus);
    }

    @Transactional
    public AuthorizationResponse create(String productCode, AuthorizationCreateRequest request) {
        String normalizedProductCode = requireText(productCode, "productCode");
        AuthorizationEligibilityResponse eligibility = getEligibility(normalizedProductCode);
        if (!eligibility.isEligible()) {
            throw new IllegalArgumentException(eligibility.getMessage());
        }

        LocalDateTime now = LocalDateTime.now();
        Authorization authorization = new Authorization(
                generateRequestId(),
                normalizedProductCode,
                now,
                null,
                false,
                requireText(request.getRequestReason(), "requestReason"),
                requireText(request.getSubmissionAgencyName(), "submissionAgencyName"),
                AuthorizationStatus.REQUESTED,
                normalizeOptionalText(request.getProductDescriptionFileName()),
                normalizeOptionalText(request.getTermsAndConditionsFileName()),
                normalizeOptionalText(request.getRateScheduleFileName()),
                normalizeOptionalText(request.getProductEvidenceFileName()),
                null,
                now
        );

        authorizationMapper.insertAuthorization(authorization);
        productMapper.updateProductStatus(normalizedProductCode, ProductStatus.AUTHORIZATION_REQUESTED.name());

        return AuthorizationResponse.from(authorization, ProductStatus.AUTHORIZATION_REQUESTED);
    }

    @Transactional
    public AuthorizationResponse approve(String productCode) {
        return transitionState(productCode, AuthorizationStatus.APPROVED, ProductStatus.AUTHORIZED, true, null);
    }

    @Transactional
    public AuthorizationResponse reject(String productCode) {
        return transitionState(productCode, AuthorizationStatus.REJECTED, ProductStatus.AUTHORIZATION_REJECTED, false, null);
    }

    @Transactional
    public AuthorizationResponse requestRevision(String productCode, AuthorizationRevisionRequest request) {
        String revisionRequest = requireText(request.getRevisionRequest(), "revisionRequest");
        return transitionState(productCode, AuthorizationStatus.REVISION_REQUIRED,
                ProductStatus.REVISION_REQUESTED, false, revisionRequest);
    }

    @Transactional
    public AuthorizationResponse cancel(String productCode) {
        return transitionState(productCode, AuthorizationStatus.CANCELLED, ProductStatus.DESIGN_COMPLETED, false, null);
    }

    private AuthorizationResponse transitionState(
            String productCode,
            AuthorizationStatus nextAuthorizationStatus,
            ProductStatus nextProductStatus,
            boolean isApproved,
            String revisionRequest
    ) {
        String normalizedProductCode = requireText(productCode, "productCode");
        Authorization existing = authorizationMapper.findLatestByProductCode(normalizedProductCode);
        if (existing == null) {
            throw new NoSuchElementException("Authorization not found for product: " + normalizedProductCode);
        }
        if (existing.getAuthorizationStatus() != AuthorizationStatus.REQUESTED) {
            throw new IllegalArgumentException("Authorization is not in REQUESTED state.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime approvedAt = isApproved ? now : null;
        int updated = authorizationMapper.updateAuthorizationStatus(
                existing.getRequestId(),
                nextAuthorizationStatus.name(),
                isApproved,
                approvedAt,
                revisionRequest,
                now
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Failed to update authorization state.");
        }

        productMapper.updateProductStatus(normalizedProductCode, nextProductStatus.name());
        return getLatest(normalizedProductCode);
    }

    private ProductStatus loadProductStatus(String productCode) {
        AuthorizationEligibilityResponse eligibility =
                authorizationMapper.findEligibilityByProductCode(productCode);
        return eligibility == null ? null : eligibility.getProductStatus();
    }

    private String buildEligibilityMessage(ProductStatus productStatus, boolean eligible) {
        if (eligible) {
            return "Authorization can be requested for this product.";
        }
        if (productStatus == null) {
            return "Product status is unknown.";
        }
        return switch (productStatus) {
            case AUTHORIZATION_REQUESTED -> "Authorization is already in progress.";
            case AUTHORIZED -> "Product is already authorized.";
            case AUTHORIZATION_REJECTED -> "Authorization was rejected. Revise the product before re-requesting.";
            case TEMP_SAVED -> "Product is temporarily saved. Complete the design first.";
            default -> "Product is not eligible for authorization in its current state.";
        };
    }

    private String generateRequestId() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "AUTH-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
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
