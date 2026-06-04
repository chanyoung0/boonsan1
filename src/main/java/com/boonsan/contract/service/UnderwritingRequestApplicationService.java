package com.boonsan.contract.service;

import com.boonsan.contract.dto.UnderwritingRequestCompleteRequest;
import com.boonsan.contract.dto.UnderwritingRequestResponse;
import com.boonsan.contract.mapper.UnderwritingRequestMapper;
import com.boonsan.enums.RequestReason;
import com.boonsan.enums.RequestStatus;
import com.boonsan.enums.UnderwritingResultType;
import com.boonsan.enums.UnderwritingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UnderwritingRequestApplicationService {

    private final UnderwritingRequestMapper underwritingRequestMapper;

    public UnderwritingRequestApplicationService(UnderwritingRequestMapper underwritingRequestMapper) {
        this.underwritingRequestMapper = underwritingRequestMapper;
    }

    @Transactional
    public UnderwritingRequestResponse createForSource(
            String policyNumber,
            RequestReason requestReason,
            String sourceId,
            UnderwritingType underwritingType
    ) {
        if (requestReason != RequestReason.ENDORSEMENT && requestReason != RequestReason.REINSTATEMENT) {
            throw new IllegalArgumentException(
                    "Only ENDORSEMENT or REINSTATEMENT requestReason supported. Got: " + requestReason);
        }

        String requestId = generateRequestId();
        underwritingRequestMapper.insertRequest(
                requestId,
                policyNumber,
                requestReason.name(),
                sourceId,
                underwritingType == null ? null : underwritingType.name(),
                RequestStatus.PENDING.name(),
                LocalDateTime.now()
        );
        return requireById(requestId);
    }

    @Transactional
    public UnderwritingRequestResponse complete(String requestId, UnderwritingRequestCompleteRequest request) {
        UnderwritingRequestResponse existing = requireById(requestId);
        if (existing.getRequestStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Underwriting request can be completed only from PENDING. Current: " + existing.getRequestStatus());
        }
        if (request.getUnderwritingResult() == null) {
            throw new IllegalArgumentException("underwritingResult is required.");
        }
        if (request.getUnderwritingResult() == UnderwritingResultType.REJECTED
                && request.getRejectionReason() == null) {
            throw new IllegalArgumentException("rejectionReason is required when result is REJECTED.");
        }
        if (request.getUnderwritingResult() == UnderwritingResultType.SURCHARGE
                && request.getSurchargeCondition() == null) {
            throw new IllegalArgumentException("surchargeCondition is required when result is SURCHARGE.");
        }

        int updated = underwritingRequestMapper.updateCompleteResult(
                requestId,
                request.getUnderwritingResult().name(),
                request.getRejectionReason() == null ? null : request.getRejectionReason().name(),
                request.getSurchargeCondition() == null ? null : request.getSurchargeCondition().name(),
                LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Underwriting request complete failed (concurrent modification): " + requestId);
        }
        return requireById(requestId);
    }

    @Transactional
    public UnderwritingRequestResponse cancel(String requestId) {
        UnderwritingRequestResponse existing = requireById(requestId);
        if (existing.getRequestStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Underwriting request can be cancelled only from PENDING. Current: " + existing.getRequestStatus());
        }
        int updated = underwritingRequestMapper.updateStatusToCancelled(requestId, LocalDateTime.now());
        if (updated == 0) {
            throw new IllegalArgumentException(
                    "Underwriting request cancel failed (concurrent modification): " + requestId);
        }
        return requireById(requestId);
    }

    @Transactional(readOnly = true)
    public UnderwritingRequestResponse findById(String requestId) {
        return requireById(requestId);
    }

    @Transactional(readOnly = true)
    public List<UnderwritingRequestResponse> findBySourceId(String sourceId) {
        return underwritingRequestMapper.findBySourceId(sourceId);
    }

    public UnderwritingRequestResponse requireById(String requestId) {
        UnderwritingRequestResponse response = underwritingRequestMapper.findById(requestId);
        if (response == null) {
            throw new NoSuchElementException("Underwriting request not found: " + requestId);
        }
        return response;
    }

    private String generateRequestId() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "REQ-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }
}
