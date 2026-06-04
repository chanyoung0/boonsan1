package com.boonsan.domain.accident.service;

import com.boonsan.domain.accident.dto.ObjectionCreateRequest;
import com.boonsan.domain.accident.dto.ObjectionEligibilityResponse;
import com.boonsan.domain.accident.dto.ObjectionResponse;
import com.boonsan.domain.accident.mapper.ObjectionMapper;
import com.boonsan.domain.enums.AcceptanceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ObjectionApplicationService {

    private static final String RECEIVED_STATUS = AcceptanceStatus.RECEIVED.name();
    private static final String REINVESTIGATION_REQUIRED_STATUS = AcceptanceStatus.REINVESTIGATION_REQUIRED.name();
    private static final String REJECTED_STATUS = AcceptanceStatus.REJECTED.name();
    private static final String TRANSFERRED_TO_LEGAL_STATUS = AcceptanceStatus.TRANSFERRED_TO_LEGAL.name();
    private static final String COMPLETED_STATUS = AcceptanceStatus.COMPLETED.name();

    private static final String PAYMENT_REJECTED_STATUS = "REJECTED";
    private static final String PAYMENT_PAID_STATUS = "PAID";
    private static final String ACCIDENT_COMPLETED_STATUS = "COMPLETED";

    private final ObjectionMapper objectionMapper;

    public ObjectionApplicationService(ObjectionMapper objectionMapper) {
        this.objectionMapper = objectionMapper;
    }

    @Transactional(readOnly = true)
    public ObjectionEligibilityResponse getEligibility(String accidentNumber) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        ObjectionEligibilityResponse response =
                objectionMapper.findEligibilityByAccidentNumber(normalizedAccidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Accident report not found: " + normalizedAccidentNumber);
        }

        boolean hasPaymentResult = response.getDocumentId() != null;
        boolean paymentRejected = PAYMENT_REJECTED_STATUS.equals(response.getPaymentStatus());
        boolean paymentPaid = PAYMENT_PAID_STATUS.equals(response.getPaymentStatus());
        boolean accidentCompleted = ACCIDENT_COMPLETED_STATUS.equals(response.getAccidentStatus());
        response.setEligible(hasPaymentResult && (paymentRejected || paymentPaid || accidentCompleted));
        response.setUnavailableReason(buildUnavailableReason(response, hasPaymentResult));
        return response;
    }

    @Transactional(readOnly = true)
    public ObjectionResponse getObjection(String accidentNumber) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        ObjectionResponse response = objectionMapper.findObjectionByAccidentNumber(normalizedAccidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Objection not found: " + normalizedAccidentNumber);
        }
        return response;
    }

    @Transactional
    public ObjectionResponse createObjection(String accidentNumber, ObjectionCreateRequest request) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        ObjectionEligibilityResponse eligibility = getEligibility(normalizedAccidentNumber);
        if (!eligibility.isEligible()) {
            throw new IllegalArgumentException(eligibility.getUnavailableReason());
        }

        ObjectionResponse existing = objectionMapper.findObjectionByAccidentNumber(normalizedAccidentNumber);
        if (existing != null) {
            throw new IllegalArgumentException("Objection already exists: " + normalizedAccidentNumber);
        }

        LocalDateTime createdAt = LocalDateTime.now();
        objectionMapper.insertObjection(
                generateId(),
                normalizedAccidentNumber,
                requireText(request.getClaimantName(), "claimantName"),
                requireText(request.getClaimantPhone(), "claimantPhone"),
                requireText(request.getObjectionReason(), "objectionReason"),
                requireText(request.getRequestedAction(), "requestedAction"),
                requireText(request.getEmployeeNo(), "employeeNo"),
                RECEIVED_STATUS,
                createdAt
        );
        return getObjection(normalizedAccidentNumber);
    }

    @Transactional
    public ObjectionResponse markReinvestigationRequired(String accidentNumber) {
        return updateStatus(accidentNumber, REINVESTIGATION_REQUIRED_STATUS, false);
    }

    @Transactional
    public ObjectionResponse rejectObjection(String accidentNumber) {
        return updateStatus(accidentNumber, REJECTED_STATUS, false);
    }

    @Transactional
    public ObjectionResponse transferToLegal(String accidentNumber) {
        return updateStatus(accidentNumber, TRANSFERRED_TO_LEGAL_STATUS, false);
    }

    @Transactional
    public ObjectionResponse completeObjection(String accidentNumber) {
        return updateStatus(accidentNumber, COMPLETED_STATUS, true);
    }

    private ObjectionResponse updateStatus(String accidentNumber, String nextStatus, boolean complete) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        ObjectionResponse existing = getObjection(normalizedAccidentNumber);
        if (!RECEIVED_STATUS.equals(existing.getObjectionStatus())) {
            throw new IllegalArgumentException("Only received objection can be reviewed.");
        }

        LocalDateTime updatedAt = LocalDateTime.now();
        int updated = objectionMapper.updateObjectionStatus(
                normalizedAccidentNumber,
                RECEIVED_STATUS,
                nextStatus,
                updatedAt,
                complete
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Objection is not in received status.");
        }
        return getObjection(normalizedAccidentNumber);
    }

    private String buildUnavailableReason(ObjectionEligibilityResponse response, boolean hasPaymentResult) {
        if (!hasPaymentResult) {
            return "Payment approval document is required before objection.";
        }
        if (response.isEligible()) {
            return null;
        }
        return "Objection is available after payment is rejected, paid, or accident is completed.";
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String generateId() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "OBJ-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }
}
