package com.boonsan.underwriting.service;

import com.boonsan.enums.ApprovalStatus;
import com.boonsan.enums.UnderwritingResultType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.boonsan.underwriting.dto.CoinsuranceCreateRequest;
import com.boonsan.underwriting.dto.CoinsuranceProcessResponse;
import com.boonsan.underwriting.dto.CoinsuranceResultRequest;
import com.boonsan.underwriting.dto.CreditInformationInquiryResponse;
import com.boonsan.underwriting.dto.PolicyIssueResponse;
import com.boonsan.underwriting.dto.ReinsuranceCreateRequest;
import com.boonsan.underwriting.dto.ReinsuranceProcessResponse;
import com.boonsan.underwriting.dto.ReinsuranceResultRequest;
import com.boonsan.underwriting.dto.UnderwritingApplicationResponse;
import com.boonsan.underwriting.dto.UnderwritingFollowUpEligibilityResponse;
import com.boonsan.underwriting.dto.UnderwritingReviewResponse;
import com.boonsan.underwriting.mapper.CreditInformationInquiryMapper;
import com.boonsan.underwriting.mapper.UnderwritingFollowUpMapper;
import com.boonsan.underwriting.mapper.UnderwritingMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UnderwritingFollowUpApplicationService {

    private static final String REQUESTED = "REQUESTED";
    private static final String PENDING_APPROVAL = ApprovalStatus.PENDING_APPROVAL.name();
    private static final String APPROVED = ApprovalStatus.APPROVED.name();
    private static final String REJECTED = ApprovalStatus.REJECTED.name();
    private static final String ACCEPTED = "ACCEPTED";
    private static final String ISSUED = "ISSUED";
    private static final BigDecimal HIGH_INSURED_AMOUNT_THRESHOLD = BigDecimal.valueOf(500_000_000L);
    private static final String COINSURANCE_EXTERNAL_MESSAGE =
            "[외부 연동 예정] 공동인수사 참여 요청 API는 아직 실제 연동하지 않습니다.";
    private static final String REINSURANCE_EXTERNAL_MESSAGE =
            "[외부 연동 예정] 재보험사 출재 요청 API는 아직 실제 연동하지 않습니다.";
    private static final String POLICY_EXTERNAL_MESSAGE =
            "[계약 관리 연동 예정] 증권 발행 결과만 저장하며 계약 생성/PDF 발행은 아직 수행하지 않습니다.";

    private final UnderwritingMapper underwritingMapper;
    private final UnderwritingFollowUpMapper followUpMapper;
    private final CreditInformationInquiryMapper creditInformationInquiryMapper;

    public UnderwritingFollowUpApplicationService(
            UnderwritingMapper underwritingMapper,
            UnderwritingFollowUpMapper followUpMapper,
            CreditInformationInquiryMapper creditInformationInquiryMapper
    ) {
        this.underwritingMapper = underwritingMapper;
        this.followUpMapper = followUpMapper;
        this.creditInformationInquiryMapper = creditInformationInquiryMapper;
    }

    @Transactional(readOnly = true)
    public UnderwritingFollowUpEligibilityResponse getCoinsuranceEligibility(String applicationId) {
        UnderwritingContext context = requireUnderwritingContext(applicationId);
        CoinsuranceProcessResponse existing = followUpMapper.findCoinsuranceByApplicationId(context.applicationId);
        UnderwritingFollowUpEligibilityResponse response = baseEligibility(context);
        response.setCoinsuranceRecommended(context.review.isCoinsuranceRecommended());
        if (!context.isFinalAccepted()) {
            response.setEligible(false);
            response.setReason("최종 심사 결과가 승인 또는 할증인 청약만 공동인수 처리할 수 있습니다.");
        } else if (existing != null) {
            response.setEligible(false);
            response.setReason("이미 공동인수 요청이 등록된 청약입니다.");
            response.setProcessStatus(existing.getRequestStatus());
            response.setResultStatus(existing.getResultStatus());
        } else if (!context.review.isCoinsuranceRecommended()) {
            response.setEligible(false);
            response.setReason("공동인수 추천 대상은 아니지만, 업무 판단에 따라 수동 선택으로 요청할 수 있습니다.");
        } else {
            response.setEligible(true);
            response.setReason("공동인수 추천 청약으로 요청을 생성할 수 있습니다.");
        }
        response.setNextStepMessage(COINSURANCE_EXTERNAL_MESSAGE);
        return response;
    }

    @Transactional
    public CoinsuranceProcessResponse createCoinsurance(String applicationId, CoinsuranceCreateRequest request) {
        UnderwritingContext context = requireUnderwritingContext(applicationId);
        if (!context.isFinalAccepted()) {
            throw new IllegalArgumentException("Only approved or surcharged applications can request coinsurance.");
        }
        if (!context.review.isCoinsuranceRecommended() && !request.isManualSelected()) {
            throw new IllegalArgumentException("Coinsurance is not recommended. Enable manual selection to request it.");
        }
        if (followUpMapper.findCoinsuranceByApplicationId(context.applicationId) != null) {
            throw new IllegalArgumentException("Coinsurance process already exists: " + context.applicationId);
        }
        LocalDateTime now = LocalDateTime.now();
        followUpMapper.insertCoinsuranceProcess(
                generateId("COI"),
                context.applicationId,
                requireText(request.getCoinsurerName(), "coinsurerName"),
                REQUESTED,
                PENDING_APPROVAL,
                nullToZero(request.getRetainedAmount()),
                nullToZero(request.getShareRate()),
                request.isManualSelected(),
                COINSURANCE_EXTERNAL_MESSAGE,
                now,
                now
        );
        return requireCoinsurance(context.applicationId);
    }

    @Transactional(readOnly = true)
    public CoinsuranceProcessResponse getCoinsurance(String applicationId) {
        String normalizedApplicationId = requireApplication(applicationId).getApplicationId();
        return requireCoinsurance(normalizedApplicationId);
    }

    @Transactional
    public CoinsuranceProcessResponse updateCoinsuranceResult(String applicationId, CoinsuranceResultRequest request) {
        String normalizedApplicationId = requireApplication(applicationId).getApplicationId();
        requireCoinsurance(normalizedApplicationId);
        String resultStatus = parseCoinsuranceResult(request.getResultStatus());
        LocalDateTime now = LocalDateTime.now();
        int updated = followUpMapper.updateCoinsuranceResult(
                normalizedApplicationId,
                resultStatus,
                normalizeOptionalText(request.getRejectionReason()),
                now,
                now
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Coinsurance result is already finalized: " + normalizedApplicationId);
        }
        return requireCoinsurance(normalizedApplicationId);
    }

    @Transactional(readOnly = true)
    public UnderwritingFollowUpEligibilityResponse getReinsuranceEligibility(String applicationId) {
        UnderwritingContext context = requireUnderwritingContext(applicationId);
        ReinsuranceProcessResponse existing = followUpMapper.findReinsuranceByApplicationId(context.applicationId);
        ReinsuranceDecision decision = decideReinsurance(context);
        UnderwritingFollowUpEligibilityResponse response = baseEligibility(context);
        response.setReinsuranceRequired(decision.required);
        if (!context.isFinalAccepted()) {
            response.setEligible(false);
            response.setReason("최종 심사 결과가 승인 또는 할증인 청약만 재보험 처리할 수 있습니다.");
        } else if (existing != null) {
            response.setEligible(false);
            response.setReason("이미 재보험 요청이 등록된 청약입니다.");
            response.setProcessStatus(existing.getRequestStatus());
            response.setResultStatus(existing.getResultStatus());
        } else if (!decision.required) {
            response.setEligible(false);
            response.setReason(decision.reason);
        } else {
            response.setEligible(true);
            response.setReason(decision.reason);
        }
        response.setNextStepMessage(REINSURANCE_EXTERNAL_MESSAGE);
        return response;
    }

    @Transactional
    public ReinsuranceProcessResponse createReinsurance(String applicationId, ReinsuranceCreateRequest request) {
        UnderwritingContext context = requireUnderwritingContext(applicationId);
        ReinsuranceDecision decision = decideReinsurance(context);
        if (!context.isFinalAccepted()) {
            throw new IllegalArgumentException("Only approved or surcharged applications can request reinsurance.");
        }
        if (!decision.required) {
            throw new IllegalArgumentException("Reinsurance is not required for this application.");
        }
        if (followUpMapper.findReinsuranceByApplicationId(context.applicationId) != null) {
            throw new IllegalArgumentException("Reinsurance process already exists: " + context.applicationId);
        }
        LocalDateTime now = LocalDateTime.now();
        followUpMapper.insertReinsuranceProcess(
                generateId("REI"),
                context.applicationId,
                true,
                decision.reason,
                requireText(request.getReinsurerName(), "reinsurerName"),
                REQUESTED,
                REQUESTED,
                nullToZero(request.getRetentionAmount()),
                nullToZero(request.getCessionRate()),
                REINSURANCE_EXTERNAL_MESSAGE,
                now,
                now
        );
        return requireReinsurance(context.applicationId);
    }

    @Transactional(readOnly = true)
    public ReinsuranceProcessResponse getReinsurance(String applicationId) {
        String normalizedApplicationId = requireApplication(applicationId).getApplicationId();
        return requireReinsurance(normalizedApplicationId);
    }

    @Transactional
    public ReinsuranceProcessResponse updateReinsuranceResult(String applicationId, ReinsuranceResultRequest request) {
        String normalizedApplicationId = requireApplication(applicationId).getApplicationId();
        requireReinsurance(normalizedApplicationId);
        String resultStatus = parseReinsuranceResult(request.getResultStatus());
        LocalDateTime now = LocalDateTime.now();
        int updated = followUpMapper.updateReinsuranceResult(
                normalizedApplicationId,
                resultStatus,
                normalizeOptionalText(request.getRejectionReason()),
                now,
                now
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Reinsurance result is already finalized: " + normalizedApplicationId);
        }
        return requireReinsurance(normalizedApplicationId);
    }

    @Transactional(readOnly = true)
    public UnderwritingFollowUpEligibilityResponse getPolicyIssueEligibility(String applicationId) {
        UnderwritingContext context = requireUnderwritingContext(applicationId);
        PolicyIssueResponse existing = followUpMapper.findPolicyIssueByApplicationId(context.applicationId);
        UnderwritingFollowUpEligibilityResponse response = baseEligibility(context);
        response.setCoinsuranceRecommended(context.review.isCoinsuranceRecommended());
        response.setReinsuranceRequired(decideReinsurance(context).required);
        if (existing != null) {
            response.setEligible(false);
            response.setReason("이미 증권이 발행된 청약입니다.");
            response.setPolicyNumber(existing.getPolicyNumber());
            response.setResultStatus(existing.getIssueStatus());
        } else {
            PolicyBlock block = findPolicyBlock(context);
            response.setEligible(block == null);
            response.setReason(block == null ? "증권 발행이 가능합니다." : block.message);
        }
        response.setNextStepMessage(POLICY_EXTERNAL_MESSAGE);
        return response;
    }

    @Transactional
    public PolicyIssueResponse issuePolicy(String applicationId) {
        UnderwritingContext context = requireUnderwritingContext(applicationId);
        if (followUpMapper.findPolicyIssueByApplicationId(context.applicationId) != null) {
            throw new IllegalArgumentException("Policy already issued: " + context.applicationId);
        }
        PolicyBlock block = findPolicyBlock(context);
        if (block != null) {
            throw new IllegalArgumentException(block.message);
        }
        String policyNumber = generateId("POL");
        LocalDateTime issuedAt = LocalDateTime.now();
        followUpMapper.insertPolicyIssue(
                generateId("ISS"),
                context.applicationId,
                policyNumber,
                ISSUED,
                context.review.getFinalResult(),
                context.application.getAppliedCondition(),
                POLICY_EXTERNAL_MESSAGE,
                issuedAt
        );
        return requirePolicyIssue(context.applicationId);
    }

    @Transactional(readOnly = true)
    public PolicyIssueResponse getPolicyIssue(String applicationId) {
        String normalizedApplicationId = requireApplication(applicationId).getApplicationId();
        return requirePolicyIssue(normalizedApplicationId);
    }

    private PolicyBlock findPolicyBlock(UnderwritingContext context) {
        if (!context.isFinalAccepted()) {
            return new PolicyBlock("최종 심사 결과가 승인 또는 할증인 청약만 증권 발행할 수 있습니다.");
        }
        CoinsuranceProcessResponse coinsurance = followUpMapper.findCoinsuranceByApplicationId(context.applicationId);
        if (context.review.isCoinsuranceRecommended()) {
            if (coinsurance == null) {
                return new PolicyBlock("공동인수 추천 청약은 공동인수 결과가 승인되어야 증권 발행할 수 있습니다.");
            }
            if (!APPROVED.equals(coinsurance.getResultStatus())) {
                return new PolicyBlock("공동인수 결과가 승인 상태가 아닙니다.");
            }
        }
        ReinsuranceDecision decision = decideReinsurance(context);
        ReinsuranceProcessResponse reinsurance = followUpMapper.findReinsuranceByApplicationId(context.applicationId);
        if (decision.required) {
            if (reinsurance == null) {
                return new PolicyBlock("재보험 필요 청약은 재보험 결과가 수락되어야 증권 발행할 수 있습니다.");
            }
            if (!ACCEPTED.equals(reinsurance.getResultStatus())) {
                return new PolicyBlock("재보험 결과가 수락 상태가 아닙니다.");
            }
        }
        return null;
    }

    private ReinsuranceDecision decideReinsurance(UnderwritingContext context) {
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

    private UnderwritingFollowUpEligibilityResponse baseEligibility(UnderwritingContext context) {
        UnderwritingFollowUpEligibilityResponse response = new UnderwritingFollowUpEligibilityResponse();
        response.setApplicationId(context.applicationId);
        response.setApplicationStatus(context.application.getApplicationStatus());
        response.setFinalResult(context.review.getFinalResult());
        response.setTotalScore(context.review.getTotalScore());
        return response;
    }

    private UnderwritingContext requireUnderwritingContext(String applicationId) {
        UnderwritingApplicationResponse application = requireApplication(applicationId);
        UnderwritingReviewResponse review = underwritingMapper.findLatestReviewByApplicationId(application.getApplicationId());
        if (review == null || review.getFinalResult() == null) {
            throw new IllegalArgumentException("Final underwriting result is required: " + application.getApplicationId());
        }
        return new UnderwritingContext(application, review);
    }

    private UnderwritingApplicationResponse requireApplication(String applicationId) {
        String normalizedApplicationId = requireText(applicationId, "applicationId");
        UnderwritingApplicationResponse application = underwritingMapper.findApplicationById(normalizedApplicationId);
        if (application == null) {
            throw new NoSuchElementException("Insurance application not found: " + normalizedApplicationId);
        }
        return application;
    }

    private CoinsuranceProcessResponse requireCoinsurance(String applicationId) {
        CoinsuranceProcessResponse response = followUpMapper.findCoinsuranceByApplicationId(applicationId);
        if (response == null) {
            throw new NoSuchElementException("Coinsurance process not found: " + applicationId);
        }
        return response;
    }

    private ReinsuranceProcessResponse requireReinsurance(String applicationId) {
        ReinsuranceProcessResponse response = followUpMapper.findReinsuranceByApplicationId(applicationId);
        if (response == null) {
            throw new NoSuchElementException("Reinsurance process not found: " + applicationId);
        }
        return response;
    }

    private PolicyIssueResponse requirePolicyIssue(String applicationId) {
        PolicyIssueResponse response = followUpMapper.findPolicyIssueByApplicationId(applicationId);
        if (response == null) {
            throw new NoSuchElementException("Policy issue not found: " + applicationId);
        }
        return response;
    }

    private String parseCoinsuranceResult(String value) {
        String normalized = requireText(value, "resultStatus");
        if (APPROVED.equals(normalized) || REJECTED.equals(normalized)) {
            return normalized;
        }
        throw new IllegalArgumentException("resultStatus must be APPROVED or REJECTED.");
    }

    private String parseReinsuranceResult(String value) {
        String normalized = requireText(value, "resultStatus");
        if (ACCEPTED.equals(normalized) || REJECTED.equals(normalized)) {
            return normalized;
        }
        throw new IllegalArgumentException("resultStatus must be ACCEPTED or REJECTED.");
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
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

    private static class UnderwritingContext {
        private final String applicationId;
        private final UnderwritingApplicationResponse application;
        private final UnderwritingReviewResponse review;

        private UnderwritingContext(UnderwritingApplicationResponse application, UnderwritingReviewResponse review) {
            this.applicationId = application.getApplicationId();
            this.application = application;
            this.review = review;
        }

        private boolean isFinalAccepted() {
            return UnderwritingResultType.APPROVED.name().equals(review.getFinalResult())
                    || UnderwritingResultType.SURCHARGE.name().equals(review.getFinalResult());
        }
    }

    private static class ReinsuranceDecision {
        private final boolean required;
        private final String reason;

        private ReinsuranceDecision(boolean required, String reason) {
            this.required = required;
            this.reason = reason;
        }
    }

    private static class PolicyBlock {
        private final String message;

        private PolicyBlock(String message) {
            this.message = message;
        }
    }
}
