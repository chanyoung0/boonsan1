package com.boonsan.domain.underwriting.service;

import com.boonsan.domain.enums.ApprovalStatus;
import com.boonsan.domain.underwriting.dto.CoinsuranceProcessResponse;
import com.boonsan.domain.underwriting.dto.PolicyIssueResponse;
import com.boonsan.domain.underwriting.dto.ReinsuranceProcessResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingFollowUpEligibilityResponse;
import com.boonsan.domain.underwriting.mapper.UnderwritingFollowUpMapper;
import com.boonsan.domain.underwriting.service.UnderwritingFollowUpSupport.ReinsuranceDecision;
import com.boonsan.domain.underwriting.service.UnderwritingFollowUpSupport.UnderwritingContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

// 증권발행 유스케이스(발행 자격 조회/발행/조회)를 담당하는 서비스
@Service
public class PolicyIssuanceApplicationService {

    private static final String APPROVED = ApprovalStatus.APPROVED.name();
    private static final String ACCEPTED = "ACCEPTED";
    private static final String ISSUED = "ISSUED";
    private static final String POLICY_EXTERNAL_MESSAGE =
            "[계약 관리 연동 예정] 증권 발행 결과만 저장하며 계약 생성/PDF 발행은 아직 수행하지 않습니다.";

    private final UnderwritingFollowUpMapper followUpMapper;
    private final UnderwritingFollowUpSupport support;

    public PolicyIssuanceApplicationService(
            UnderwritingFollowUpMapper followUpMapper,
            UnderwritingFollowUpSupport support
    ) {
        this.followUpMapper = followUpMapper;
        this.support = support;
    }

    @Transactional(readOnly = true)
    public UnderwritingFollowUpEligibilityResponse getPolicyIssueEligibility(String applicationId) {
        UnderwritingContext context = support.requireUnderwritingContext(applicationId);
        PolicyIssueResponse existing = followUpMapper.findPolicyIssueByApplicationId(context.getApplicationId());
        UnderwritingFollowUpEligibilityResponse response = support.baseEligibility(context);
        response.setCoinsuranceRecommended(context.getReview().isCoinsuranceRecommended());
        response.setReinsuranceRequired(support.decideReinsurance(context).isRequired());
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
        UnderwritingContext context = support.requireUnderwritingContext(applicationId);
        if (followUpMapper.findPolicyIssueByApplicationId(context.getApplicationId()) != null) {
            throw new IllegalArgumentException("Policy already issued: " + context.getApplicationId());
        }
        PolicyBlock block = findPolicyBlock(context);
        if (block != null) {
            throw new IllegalArgumentException(block.message);
        }
        String policyNumber = support.generateId("POL");
        LocalDateTime issuedAt = LocalDateTime.now();
        followUpMapper.insertPolicyIssue(
                support.generateId("ISS"),
                context.getApplicationId(),
                policyNumber,
                ISSUED,
                context.getReview().getFinalResult(),
                context.getApplication().getAppliedCondition(),
                POLICY_EXTERNAL_MESSAGE,
                issuedAt
        );
        return requirePolicyIssue(context.getApplicationId());
    }

    @Transactional(readOnly = true)
    public PolicyIssueResponse getPolicyIssue(String applicationId) {
        String normalizedApplicationId = support.requireApplication(applicationId).getApplicationId();
        return requirePolicyIssue(normalizedApplicationId);
    }

    private PolicyBlock findPolicyBlock(UnderwritingContext context) {
        if (!context.isFinalAccepted()) {
            return new PolicyBlock("최종 심사 결과가 승인 또는 할증인 청약만 증권 발행할 수 있습니다.");
        }
        CoinsuranceProcessResponse coinsurance = followUpMapper.findCoinsuranceByApplicationId(context.getApplicationId());
        if (context.getReview().isCoinsuranceRecommended()) {
            if (coinsurance == null) {
                return new PolicyBlock("공동인수 추천 청약은 공동인수 결과가 승인되어야 증권 발행할 수 있습니다.");
            }
            if (!APPROVED.equals(coinsurance.getResultStatus())) {
                return new PolicyBlock("공동인수 결과가 승인 상태가 아닙니다.");
            }
        }
        ReinsuranceDecision decision = support.decideReinsurance(context);
        ReinsuranceProcessResponse reinsurance = followUpMapper.findReinsuranceByApplicationId(context.getApplicationId());
        if (decision.isRequired()) {
            if (reinsurance == null) {
                return new PolicyBlock("재보험 필요 청약은 재보험 결과가 수락되어야 증권 발행할 수 있습니다.");
            }
            if (!ACCEPTED.equals(reinsurance.getResultStatus())) {
                return new PolicyBlock("재보험 결과가 수락 상태가 아닙니다.");
            }
        }
        return null;
    }

    private PolicyIssueResponse requirePolicyIssue(String applicationId) {
        PolicyIssueResponse response = followUpMapper.findPolicyIssueByApplicationId(applicationId);
        if (response == null) {
            throw new NoSuchElementException("Policy issue not found: " + applicationId);
        }
        return response;
    }

    private static class PolicyBlock {
        private final String message;

        private PolicyBlock(String message) {
            this.message = message;
        }
    }
}
