package claim.service;

import claim.dto.SubrogationCompleteRequest;
import claim.dto.SubrogationCreateRequest;
import claim.dto.SubrogationEligibilityResponse;
import claim.dto.SubrogationResponse;
import claim.mapper.SubrogationMapper;
import enums.SubrogationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SubrogationApplicationService {

    private static final String PAID_STATUS = "PAID";
    private static final String COMPLETED_ACCIDENT_STATUS = "COMPLETED";
    private static final String REQUESTED_SUBROGATION_STATUS = SubrogationStatus.IN_PROGRESS.name();
    private static final String COMPLETED_SUBROGATION_STATUS = SubrogationStatus.COMPLETED.name();

    private final SubrogationMapper subrogationMapper;

    public SubrogationApplicationService(SubrogationMapper subrogationMapper) {
        this.subrogationMapper = subrogationMapper;
    }

    @Transactional(readOnly = true)
    public SubrogationEligibilityResponse getEligibility(String accidentNumber) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        SubrogationEligibilityResponse response =
                subrogationMapper.findEligibilityByAccidentNumber(normalizedAccidentNumber);

        if (response == null) {
            String accidentStatus = subrogationMapper.findAccidentStatusByAccidentNumber(normalizedAccidentNumber);
            SubrogationEligibilityResponse notEligible = new SubrogationEligibilityResponse();
            notEligible.setAccidentNumber(normalizedAccidentNumber);
            notEligible.setAccidentStatus(accidentStatus);
            notEligible.setEligible(false);
            notEligible.setMessage("Payment approval document is required before subrogation.");
            return notEligible;
        }

        boolean paymentCompleted = PAID_STATUS.equals(response.getPaymentStatus());
        boolean accidentCompleted = COMPLETED_ACCIDENT_STATUS.equals(response.getAccidentStatus());
        response.setEligible(paymentCompleted && accidentCompleted);
        response.setMessage(buildEligibilityMessage(paymentCompleted, accidentCompleted));
        return response;
    }

    @Transactional(readOnly = true)
    public SubrogationResponse getSubrogation(String accidentNumber) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        SubrogationResponse response = subrogationMapper.findSubrogationByAccidentNumber(normalizedAccidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Subrogation request not found: " + normalizedAccidentNumber);
        }
        return response;
    }

    @Transactional
    public SubrogationResponse createSubrogation(String accidentNumber, SubrogationCreateRequest request) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        SubrogationEligibilityResponse eligibility = getEligibility(normalizedAccidentNumber);
        if (!eligibility.isEligible()) {
            throw new IllegalArgumentException(eligibility.getMessage());
        }

        SubrogationResponse existing = subrogationMapper.findSubrogationByAccidentNumber(normalizedAccidentNumber);
        if (existing != null) {
            throw new IllegalArgumentException("Subrogation request already exists: " + normalizedAccidentNumber);
        }

        if (request.getSubrogationAmount().compareTo(eligibility.getPaidAmount()) > 0) {
            throw new IllegalArgumentException("Subrogation amount cannot exceed paid amount.");
        }

        LocalDateTime createdAt = LocalDateTime.now();
        subrogationMapper.insertSubrogation(
                generateId(),
                normalizedAccidentNumber,
                eligibility.getDocumentId(),
                eligibility.getInvestigationId(),
                requireText(request.getTargetName(), "targetName"),
                requireText(request.getSubrogationReason(), "subrogationReason"),
                request.getSubrogationAmount(),
                requireText(request.getEmployeeNo(), "employeeNo"),
                REQUESTED_SUBROGATION_STATUS,
                createdAt
        );

        return getSubrogation(normalizedAccidentNumber);
    }

    @Transactional
    public SubrogationResponse completeSubrogation(String accidentNumber, SubrogationCompleteRequest request) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        SubrogationResponse existing = getSubrogation(normalizedAccidentNumber);

        if (!REQUESTED_SUBROGATION_STATUS.equals(existing.getSubrogationStatus())) {
            throw new IllegalArgumentException("Only requested subrogation can be completed.");
        }
        if (request.getRecoveredAmount().compareTo(existing.getSubrogationAmount()) > 0) {
            throw new IllegalArgumentException("Recovered amount cannot exceed subrogation amount.");
        }

        LocalDateTime recoveredAt = LocalDateTime.now();
        int updated = subrogationMapper.completeSubrogation(
                normalizedAccidentNumber,
                REQUESTED_SUBROGATION_STATUS,
                COMPLETED_SUBROGATION_STATUS,
                request.getRecoveredAmount(),
                recoveredAt
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Subrogation is not in requested status.");
        }

        return getSubrogation(normalizedAccidentNumber);
    }

    private String buildEligibilityMessage(boolean paymentCompleted, boolean accidentCompleted) {
        if (paymentCompleted && accidentCompleted) {
            return "Subrogation can be requested for this paid accident.";
        }
        if (!paymentCompleted) {
            return "Insurance payment must be completed before subrogation.";
        }
        return "Accident status must be COMPLETED before subrogation.";
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String generateId() {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return "SUB-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }
}
