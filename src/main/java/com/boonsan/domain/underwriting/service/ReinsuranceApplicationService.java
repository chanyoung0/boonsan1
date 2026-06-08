package com.boonsan.domain.underwriting.service;

import com.boonsan.domain.enums.ApprovalStatus;
import com.boonsan.domain.underwriting.dto.ReinsuranceCreateRequest;
import com.boonsan.domain.underwriting.dto.ReinsuranceProcessResponse;
import com.boonsan.domain.underwriting.dto.ReinsuranceResultRequest;
import com.boonsan.domain.underwriting.dto.UnderwritingFollowUpEligibilityResponse;
import com.boonsan.domain.underwriting.mapper.UnderwritingFollowUpMapper;
import com.boonsan.domain.underwriting.service.UnderwritingFollowUpSupport.ReinsuranceDecision;
import com.boonsan.domain.underwriting.service.UnderwritingFollowUpSupport.UnderwritingContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

// 재보험 처리 유스케이스(요청 자격 조회/요청 생성/결과 갱신)를 담당하는 서비스
@Service
public class ReinsuranceApplicationService {

    private static final String REQUESTED = "REQUESTED";
    private static final String REJECTED = ApprovalStatus.REJECTED.name();
    private static final String ACCEPTED = "ACCEPTED";
    private static final String REINSURANCE_EXTERNAL_MESSAGE =
            "[외부 연동 예정] 재보험사 출재 요청 API는 아직 실제 연동하지 않습니다.";

    private final UnderwritingFollowUpMapper followUpMapper;
    private final UnderwritingFollowUpSupport support;

    public ReinsuranceApplicationService(
            UnderwritingFollowUpMapper followUpMapper,
            UnderwritingFollowUpSupport support
    ) {
        this.followUpMapper = followUpMapper;
        this.support = support;
    }

    @Transactional(readOnly = true)
    public UnderwritingFollowUpEligibilityResponse getReinsuranceEligibility(String applicationId) {
        UnderwritingContext context = support.requireUnderwritingContext(applicationId);
        ReinsuranceProcessResponse existing = followUpMapper.findReinsuranceByApplicationId(context.getApplicationId());
        ReinsuranceDecision decision = support.decideReinsurance(context);
        UnderwritingFollowUpEligibilityResponse response = support.baseEligibility(context);
        response.setReinsuranceRequired(decision.isRequired());
        if (!context.isFinalAccepted()) {
            response.setEligible(false);
            response.setReason("최종 심사 결과가 승인 또는 할증인 청약만 재보험 처리할 수 있습니다.");
        } else if (existing != null) {
            response.setEligible(false);
            response.setReason("이미 재보험 요청이 등록된 청약입니다.");
            response.setProcessStatus(existing.getRequestStatus());
            response.setResultStatus(existing.getResultStatus());
        } else if (!decision.isRequired()) {
            response.setEligible(false);
            response.setReason(decision.getReason());
        } else {
            response.setEligible(true);
            response.setReason(decision.getReason());
        }
        response.setNextStepMessage(REINSURANCE_EXTERNAL_MESSAGE);
        return response;
    }

    @Transactional
    public ReinsuranceProcessResponse createReinsurance(String applicationId, ReinsuranceCreateRequest request) {
        UnderwritingContext context = support.requireUnderwritingContext(applicationId);
        ReinsuranceDecision decision = support.decideReinsurance(context);
        if (!context.isFinalAccepted()) {
            throw new IllegalArgumentException("Only approved or surcharged applications can request reinsurance.");
        }
        if (!decision.isRequired()) {
            throw new IllegalArgumentException("Reinsurance is not required for this application.");
        }
        if (followUpMapper.findReinsuranceByApplicationId(context.getApplicationId()) != null) {
            throw new IllegalArgumentException("Reinsurance process already exists: " + context.getApplicationId());
        }
        LocalDateTime now = LocalDateTime.now();
        followUpMapper.insertReinsuranceProcess(
                support.generateId("REI"),
                context.getApplicationId(),
                true,
                decision.getReason(),
                support.requireText(request.getReinsurerName(), "reinsurerName"),
                REQUESTED,
                REQUESTED,
                support.nullToZero(request.getRetentionAmount()),
                support.nullToZero(request.getCessionRate()),
                REINSURANCE_EXTERNAL_MESSAGE,
                now,
                now
        );
        return requireReinsurance(context.getApplicationId());
    }

    @Transactional(readOnly = true)
    public ReinsuranceProcessResponse getReinsurance(String applicationId) {
        String normalizedApplicationId = support.requireApplication(applicationId).getApplicationId();
        return requireReinsurance(normalizedApplicationId);
    }

    @Transactional
    public ReinsuranceProcessResponse updateReinsuranceResult(String applicationId, ReinsuranceResultRequest request) {
        String normalizedApplicationId = support.requireApplication(applicationId).getApplicationId();
        requireReinsurance(normalizedApplicationId);
        String resultStatus = parseReinsuranceResult(request.getResultStatus());
        LocalDateTime now = LocalDateTime.now();
        int updated = followUpMapper.updateReinsuranceResult(
                normalizedApplicationId,
                resultStatus,
                support.normalizeOptionalText(request.getRejectionReason()),
                now,
                now
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Reinsurance result is already finalized: " + normalizedApplicationId);
        }
        return requireReinsurance(normalizedApplicationId);
    }

    private ReinsuranceProcessResponse requireReinsurance(String applicationId) {
        ReinsuranceProcessResponse response = followUpMapper.findReinsuranceByApplicationId(applicationId);
        if (response == null) {
            throw new NoSuchElementException("Reinsurance process not found: " + applicationId);
        }
        return response;
    }

    private String parseReinsuranceResult(String value) {
        String normalized = support.requireText(value, "resultStatus");
        if (ACCEPTED.equals(normalized) || REJECTED.equals(normalized)) {
            return normalized;
        }
        throw new IllegalArgumentException("resultStatus must be ACCEPTED or REJECTED.");
    }
}
