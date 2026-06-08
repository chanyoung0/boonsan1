package com.boonsan.domain.underwriting.service;

import com.boonsan.domain.enums.UnderwritingResultType;
import com.boonsan.domain.underwriting.dto.CreditInformationInquiryResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingApplicationResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingFollowUpEligibilityResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingReviewResponse;
import com.boonsan.domain.underwriting.mapper.CreditInformationInquiryMapper;
import com.boonsan.domain.underwriting.mapper.UnderwritingMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

// 공동인수/재보험/증권발행 서비스가 공유하는 심사 컨텍스트 로딩 및 공통 유틸리티 컴포넌트
@Component
public class UnderwritingFollowUpSupport {

    private static final BigDecimal HIGH_INSURED_AMOUNT_THRESHOLD = BigDecimal.valueOf(500_000_000L);

    private final UnderwritingMapper underwritingMapper;
    private final CreditInformationInquiryMapper creditInformationInquiryMapper;

    public UnderwritingFollowUpSupport(
            UnderwritingMapper underwritingMapper,
            CreditInformationInquiryMapper creditInformationInquiryMapper
    ) {
        this.underwritingMapper = underwritingMapper;
        this.creditInformationInquiryMapper = creditInformationInquiryMapper;
    }

    public UnderwritingContext requireUnderwritingContext(String applicationId) {
        UnderwritingApplicationResponse application = requireApplication(applicationId);
        UnderwritingReviewResponse review = underwritingMapper.findLatestReviewByApplicationId(application.getApplicationId());
        if (review == null || review.getFinalResult() == null) {
            throw new IllegalArgumentException("Final underwriting result is required: " + application.getApplicationId());
        }
        return new UnderwritingContext(application, review);
    }

    public UnderwritingApplicationResponse requireApplication(String applicationId) {
        String normalizedApplicationId = requireText(applicationId, "applicationId");
        UnderwritingApplicationResponse application = underwritingMapper.findApplicationById(normalizedApplicationId);
        if (application == null) {
            throw new NoSuchElementException("Insurance application not found: " + normalizedApplicationId);
        }
        return application;
    }

    public ReinsuranceDecision decideReinsurance(UnderwritingContext context) {
        if (context.application.getInsuredAmount() != null
                && context.application.getInsuredAmount().compareTo(HIGH_INSURED_AMOUNT_THRESHOLD) > 0) {
            return new ReinsuranceDecision(true, "보험가입금액이 자체 보유 한도를 초과하여 재보험 검토가 필요합니다.");
        }
        if (context.review.getTotalScore() < 75) {
            return new ReinsuranceDecision(true, "자동심사 점수가 75점 미만으로 재보험 검토가 필요합니다.");
        }
        List<CreditInformationInquiryResponse> inquiries =
                creditInformationInquiryMapper.findByApplicationId(context.applicationId);
        if (!inquiries.isEmpty() && "HIGH".equals(inquiries.get(0).getCreditRiskGrade())) {
            return new ReinsuranceDecision(true, "최근 신용정보 조회 위험등급이 높음으로 재보험 검토가 필요합니다.");
        }
        return new ReinsuranceDecision(false, "Mock 기준상 재보험 요청 대상이 아닙니다.");
    }

    public UnderwritingFollowUpEligibilityResponse baseEligibility(UnderwritingContext context) {
        UnderwritingFollowUpEligibilityResponse response = new UnderwritingFollowUpEligibilityResponse();
        response.setApplicationId(context.applicationId);
        response.setApplicationStatus(context.application.getApplicationStatus());
        response.setFinalResult(context.review.getFinalResult());
        response.setTotalScore(context.review.getTotalScore());
        return response;
    }

    public String generateId(String prefix) {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return prefix + "-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }

    public String requireText(String value, String fieldName) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return normalized;
    }

    public String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static class UnderwritingContext {
        final String applicationId;
        final UnderwritingApplicationResponse application;
        final UnderwritingReviewResponse review;

        UnderwritingContext(UnderwritingApplicationResponse application, UnderwritingReviewResponse review) {
            this.applicationId = application.getApplicationId();
            this.application = application;
            this.review = review;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public UnderwritingApplicationResponse getApplication() {
            return application;
        }

        public UnderwritingReviewResponse getReview() {
            return review;
        }

        public boolean isFinalAccepted() {
            return UnderwritingResultType.APPROVED.name().equals(review.getFinalResult())
                    || UnderwritingResultType.SURCHARGE.name().equals(review.getFinalResult());
        }
    }

    public static class ReinsuranceDecision {
        final boolean required;
        final String reason;

        ReinsuranceDecision(boolean required, String reason) {
            this.required = required;
            this.reason = reason;
        }

        public boolean isRequired() {
            return required;
        }

        public String getReason() {
            return reason;
        }
    }
}
