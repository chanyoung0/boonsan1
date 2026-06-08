package com.boonsan.domain.underwriting.service;

import com.boonsan.domain.enums.ApprovalStatus;
import com.boonsan.domain.underwriting.dto.CoinsuranceCreateRequest;
import com.boonsan.domain.underwriting.dto.CoinsuranceProcessResponse;
import com.boonsan.domain.underwriting.dto.CoinsuranceResultRequest;
import com.boonsan.domain.underwriting.dto.UnderwritingFollowUpEligibilityResponse;
import com.boonsan.domain.underwriting.mapper.UnderwritingFollowUpMapper;
import com.boonsan.domain.underwriting.service.UnderwritingFollowUpSupport.UnderwritingContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

// 공동인수 처리 유스케이스(요청 자격 조회/요청 생성/결과 갱신)를 담당하는 서비스
@Service
public class CoinsuranceApplicationService {

    private static final String REQUESTED = "REQUESTED";
    private static final String PENDING_APPROVAL = ApprovalStatus.PENDING_APPROVAL.name();
    private static final String APPROVED = ApprovalStatus.APPROVED.name();
    private static final String REJECTED = ApprovalStatus.REJECTED.name();
    private static final String COINSURANCE_EXTERNAL_MESSAGE =
            "[외부 연동 예정] 공동인수사 참여 요청 API는 아직 실제 연동하지 않습니다.";

    private final UnderwritingFollowUpMapper followUpMapper;
    private final UnderwritingFollowUpSupport support;

    public CoinsuranceApplicationService(
            UnderwritingFollowUpMapper followUpMapper,
            UnderwritingFollowUpSupport support
    ) {
        this.followUpMapper = followUpMapper;
        this.support = support;
    }

    @Transactional(readOnly = true)
    public UnderwritingFollowUpEligibilityResponse getCoinsuranceEligibility(String applicationId) {
        UnderwritingContext context = support.requireUnderwritingContext(applicationId);
        CoinsuranceProcessResponse existing = followUpMapper.findCoinsuranceByApplicationId(context.getApplicationId());
        UnderwritingFollowUpEligibilityResponse response = support.baseEligibility(context);
        response.setCoinsuranceRecommended(context.getReview().isCoinsuranceRecommended());
        if (!context.isFinalAccepted()) {
            response.setEligible(false);
            response.setReason("최종 심사 결과가 승인 또는 할증인 청약만 공동인수 처리할 수 있습니다.");
        } else if (existing != null) {
            response.setEligible(false);
            response.setReason("이미 공동인수 요청이 등록된 청약입니다.");
            response.setProcessStatus(existing.getRequestStatus());
            response.setResultStatus(existing.getResultStatus());
        } else if (!context.getReview().isCoinsuranceRecommended()) {
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
        UnderwritingContext context = support.requireUnderwritingContext(applicationId);
        if (!context.isFinalAccepted()) {
            throw new IllegalArgumentException("Only approved or surcharged applications can request coinsurance.");
        }
        if (!context.getReview().isCoinsuranceRecommended() && !request.isManualSelected()) {
            throw new IllegalArgumentException("Coinsurance is not recommended. Enable manual selection to request it.");
        }
        if (followUpMapper.findCoinsuranceByApplicationId(context.getApplicationId()) != null) {
            throw new IllegalArgumentException("Coinsurance process already exists: " + context.getApplicationId());
        }
        LocalDateTime now = LocalDateTime.now();
        followUpMapper.insertCoinsuranceProcess(
                support.generateId("COI"),
                context.getApplicationId(),
                support.requireText(request.getCoinsurerName(), "coinsurerName"),
                REQUESTED,
                PENDING_APPROVAL,
                support.nullToZero(request.getRetainedAmount()),
                support.nullToZero(request.getShareRate()),
                request.isManualSelected(),
                COINSURANCE_EXTERNAL_MESSAGE,
                now,
                now
        );
        return requireCoinsurance(context.getApplicationId());
    }

    @Transactional(readOnly = true)
    public CoinsuranceProcessResponse getCoinsurance(String applicationId) {
        String normalizedApplicationId = support.requireApplication(applicationId).getApplicationId();
        return requireCoinsurance(normalizedApplicationId);
    }

    @Transactional
    public CoinsuranceProcessResponse updateCoinsuranceResult(String applicationId, CoinsuranceResultRequest request) {
        String normalizedApplicationId = support.requireApplication(applicationId).getApplicationId();
        requireCoinsurance(normalizedApplicationId);
        String resultStatus = parseCoinsuranceResult(request.getResultStatus());
        LocalDateTime now = LocalDateTime.now();
        int updated = followUpMapper.updateCoinsuranceResult(
                normalizedApplicationId,
                resultStatus,
                support.normalizeOptionalText(request.getRejectionReason()),
                now,
                now
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Coinsurance result is already finalized: " + normalizedApplicationId);
        }
        return requireCoinsurance(normalizedApplicationId);
    }

    private CoinsuranceProcessResponse requireCoinsurance(String applicationId) {
        CoinsuranceProcessResponse response = followUpMapper.findCoinsuranceByApplicationId(applicationId);
        if (response == null) {
            throw new NoSuchElementException("Coinsurance process not found: " + applicationId);
        }
        return response;
    }

    private String parseCoinsuranceResult(String value) {
        String normalized = support.requireText(value, "resultStatus");
        if (APPROVED.equals(normalized) || REJECTED.equals(normalized)) {
            return normalized;
        }
        throw new IllegalArgumentException("resultStatus must be APPROVED or REJECTED.");
    }
}
