package underwriting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import underwriting.dto.CreditInformationInquiryCreateRequest;
import underwriting.dto.CreditInformationInquiryResponse;
import underwriting.dto.UnderwritingApplicationResponse;
import underwriting.mapper.CreditInformationInquiryMapper;
import underwriting.mapper.UnderwritingMapper;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CreditInformationInquiryApplicationService {

    private static final String INQUIRY_STATUS_COMPLETED = "COMPLETED";
    private static final String EXTERNAL_SYSTEM_MESSAGE =
            "[외부 연동 예정] 신용정보 조회 시스템 연동 예정 - 현재 결과는 Mock 데이터입니다.";

    private final CreditInformationInquiryMapper creditInformationInquiryMapper;
    private final UnderwritingMapper underwritingMapper;

    public CreditInformationInquiryApplicationService(
            CreditInformationInquiryMapper creditInformationInquiryMapper,
            UnderwritingMapper underwritingMapper
    ) {
        this.creditInformationInquiryMapper = creditInformationInquiryMapper;
        this.underwritingMapper = underwritingMapper;
    }

    @Transactional
    public CreditInformationInquiryResponse createInquiry(
            String applicationId,
            CreditInformationInquiryCreateRequest request
    ) {
        UnderwritingApplicationResponse application = requireApplication(applicationId);
        String normalizedApplicationId = application.getApplicationId();
        String inquiryId = generateId("CII");
        String customerName = firstNonBlank(request.getCustomerName(), application.getInsuredPersonName());
        String customerIdentifierMasked = maskIdentifier(request.getCustomerIdentifier());
        boolean accidentHistoryExists = resolveFlag(
                request.getAccidentHistoryExists(),
                normalizedApplicationId,
                "ACCIDENT_HISTORY"
        );
        boolean otherInsuranceContractExists = resolveFlag(
                request.getOtherInsuranceContractExists(),
                normalizedApplicationId,
                "OTHER_INSURANCE_CONTRACT"
        );
        boolean previousClaimExists = resolveFlag(
                request.getPreviousClaimExists(),
                normalizedApplicationId,
                "PREVIOUS_CLAIM"
        );
        int riskFlagCount = countRiskFlags(
                accidentHistoryExists,
                otherInsuranceContractExists,
                previousClaimExists
        );
        List<String> riskFlags = buildRiskFlags(
                accidentHistoryExists,
                otherInsuranceContractExists,
                previousClaimExists
        );
        String creditRiskGrade = calculateRiskGrade(riskFlagCount);
        LocalDateTime createdAt = LocalDateTime.now();

        creditInformationInquiryMapper.insertInquiry(
                inquiryId,
                normalizedApplicationId,
                customerName,
                customerIdentifierMasked,
                accidentHistoryExists,
                otherInsuranceContractExists,
                previousClaimExists,
                creditRiskGrade,
                String.join(",", riskFlags),
                INQUIRY_STATUS_COMPLETED,
                EXTERNAL_SYSTEM_MESSAGE,
                createdAt
        );

        return requireInquiry(inquiryId);
    }

    @Transactional(readOnly = true)
    public List<CreditInformationInquiryResponse> getInquiries(String applicationId) {
        String normalizedApplicationId = requireApplication(applicationId).getApplicationId();
        return creditInformationInquiryMapper.findByApplicationId(normalizedApplicationId);
    }

    @Transactional(readOnly = true)
    public CreditInformationInquiryResponse getInquiry(String inquiryId) {
        return requireInquiry(requireText(inquiryId, "inquiryId"));
    }

    private UnderwritingApplicationResponse requireApplication(String applicationId) {
        String normalizedApplicationId = requireText(applicationId, "applicationId");
        UnderwritingApplicationResponse application = underwritingMapper.findApplicationById(normalizedApplicationId);
        if (application == null) {
            throw new NoSuchElementException("Insurance application not found: " + normalizedApplicationId);
        }
        return application;
    }

    private CreditInformationInquiryResponse requireInquiry(String inquiryId) {
        CreditInformationInquiryResponse response = creditInformationInquiryMapper.findByInquiryId(inquiryId);
        if (response == null) {
            throw new NoSuchElementException("Credit information inquiry not found: " + inquiryId);
        }
        return response;
    }

    private boolean resolveFlag(Boolean requestedValue, String applicationId, String flagName) {
        if (requestedValue != null) {
            return requestedValue;
        }
        int bucket = Math.floorMod((applicationId + ":" + flagName).hashCode(), 5);
        return bucket == 0;
    }

    private List<String> buildRiskFlags(
            boolean accidentHistoryExists,
            boolean otherInsuranceContractExists,
            boolean previousClaimExists
    ) {
        List<String> flags = new ArrayList<>();
        if (accidentHistoryExists) {
            flags.add("ACCIDENT_HISTORY");
        }
        if (otherInsuranceContractExists) {
            flags.add("OTHER_INSURANCE_CONTRACT");
        }
        if (previousClaimExists) {
            flags.add("PREVIOUS_CLAIM");
        }
        if (flags.isEmpty()) {
            flags.add("NONE");
        }
        return flags;
    }

    private int countRiskFlags(
            boolean accidentHistoryExists,
            boolean otherInsuranceContractExists,
            boolean previousClaimExists
    ) {
        int count = 0;
        if (accidentHistoryExists) {
            count++;
        }
        if (otherInsuranceContractExists) {
            count++;
        }
        if (previousClaimExists) {
            count++;
        }
        return count;
    }

    private String calculateRiskGrade(int riskFlagCount) {
        if (riskFlagCount <= 0) {
            return "LOW";
        }
        if (riskFlagCount == 1) {
            return "MEDIUM";
        }
        return "HIGH";
    }

    private String firstNonBlank(String primary, String fallback) {
        String normalizedPrimary = normalizeOptionalText(primary);
        if (normalizedPrimary != null) {
            return normalizedPrimary;
        }
        return requireText(fallback, "customerName");
    }

    private String maskIdentifier(String value) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            return null;
        }
        int visibleLength = Math.min(6, normalized.length());
        return normalized.substring(0, visibleLength) + "*".repeat(normalized.length() - visibleLength);
    }

    private String generateId(String prefix) {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return prefix + "-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
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
