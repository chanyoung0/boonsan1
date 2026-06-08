package com.boonsan.domain.accident.service;

import com.boonsan.domain.accident.dto.AdjusterOpinionRequest;
import com.boonsan.domain.accident.dto.DamageAssessmentRequest;
import com.boonsan.domain.accident.dto.DamageInvestigationResultResponse;
import com.boonsan.domain.accident.dto.DamageInvestigationStartResponse;
import com.boonsan.domain.accident.dto.FieldInvestigationMaterialResponse;
import com.boonsan.domain.accident.dto.InvestigationApprovalRequest;
import com.boonsan.domain.accident.dto.PaymentApprovalDocumentResponse;
import com.boonsan.domain.accident.dto.PaymentApprovalDraftResponse;
import com.boonsan.domain.accident.mapper.DamageInvestigationMapper;
import com.boonsan.domain.model.accident.AccidentReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class DamageInvestigationApplicationService {

    private static final String PAYMENT_APPROVAL_DOCUMENT_TYPE = "PAYMENT_APPROVAL";
    private static final String DRAFT_STATUS = "DRAFT";
    private static final String OPINION_SAVED_STATUS = "OPINION_SAVED";
    private static final String APPROVAL_REQUESTED_STATUS = "APPROVAL_REQUESTED";
    private static final String APPROVED_STATUS = "APPROVED";
    private static final String REJECTED_STATUS = "REJECTED";

    private final DamageInvestigationMapper damageInvestigationMapper;

    public DamageInvestigationApplicationService(DamageInvestigationMapper damageInvestigationMapper) {
        this.damageInvestigationMapper = damageInvestigationMapper;
    }

    @Transactional(readOnly = true)
    public DamageInvestigationStartResponse getAccidentReportForInvestigation(String accidentNumber) {
        AccidentReport report = requireAccidentReport(accidentNumber);
        return DamageInvestigationStartResponse.from(report);
    }

    @Transactional(readOnly = true)
    public FieldInvestigationMaterialResponse getFieldInvestigationMaterials(String accidentNumber) {
        String normalizedAccidentNumber = requireAccidentReport(accidentNumber).getReportNo();
        String filePrefix = normalizedAccidentNumber.replaceAll("[^A-Za-z0-9-]", "_");
        return new FieldInvestigationMaterialResponse(
                filePrefix + "_scene-photo.jpg",
                filePrefix + "_blackbox-video.mp4",
                filePrefix + "_repair-estimate.pdf"
        );
    }

    @Transactional(readOnly = true)
    public DamageInvestigationResultResponse getDamageInvestigationResult(String accidentNumber) {
        String normalizedAccidentNumber = requireAccidentReport(accidentNumber).getReportNo();
        DamageInvestigationResultResponse response =
                damageInvestigationMapper.findDamageInvestigationResultByAccidentNumber(normalizedAccidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Damage investigation result not found: " + normalizedAccidentNumber);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public PaymentApprovalDocumentResponse getPaymentApprovalDocument(String accidentNumber) {
        String normalizedAccidentNumber = requireAccidentReport(accidentNumber).getReportNo();
        PaymentApprovalDocumentResponse response =
                damageInvestigationMapper.findPaymentApprovalDocumentByAccidentNumber(normalizedAccidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Payment approval document not found: " + normalizedAccidentNumber);
        }
        return response;
    }

    @Transactional
    public PaymentApprovalDocumentResponse approvePaymentApprovalDocument(String accidentNumber) {
        String normalizedAccidentNumber = requireAccidentReport(accidentNumber).getReportNo();
        updatePaymentApprovalStatus(
                normalizedAccidentNumber,
                APPROVAL_REQUESTED_STATUS,
                APPROVED_STATUS,
                "Payment approval document can only be approved from APPROVAL_REQUESTED status."
        );
        return requirePaymentApprovalDocument(normalizedAccidentNumber);
    }

    @Transactional
    public PaymentApprovalDocumentResponse rejectPaymentApprovalDocument(String accidentNumber) {
        String normalizedAccidentNumber = requireAccidentReport(accidentNumber).getReportNo();
        updatePaymentApprovalStatus(
                normalizedAccidentNumber,
                APPROVAL_REQUESTED_STATUS,
                REJECTED_STATUS,
                "Payment approval document can only be rejected from APPROVAL_REQUESTED status."
        );
        return requirePaymentApprovalDocument(normalizedAccidentNumber);
    }

    @Transactional
    public PaymentApprovalDraftResponse createPaymentApprovalDraft(
            String accidentNumber,
            DamageAssessmentRequest request
    ) {
        String normalizedAccidentNumber = requireMatchingAccidentNumber(accidentNumber, request.getAccidentNumber());
        requireAccidentReport(normalizedAccidentNumber);
        rejectDuplicatePaymentApprovalDocument(normalizedAccidentNumber);
        request.setAccidentNumber(normalizedAccidentNumber);

        LocalDateTime createdAt = LocalDateTime.now();
        String investigationId = generateId("INV");
        String documentId = generateId("PAD");
        BigDecimal totalDamageAmount = calculateTotalDamageAmount(request);
        BigDecimal calculatedPaymentAmount = calculatePaymentAmount(totalDamageAmount, request.getFaultRatio());

        damageInvestigationMapper.insertDamageInvestigation(investigationId, request, createdAt);
        damageInvestigationMapper.insertPaymentApprovalDocument(
                documentId,
                normalizedAccidentNumber,
                investigationId,
                PAYMENT_APPROVAL_DOCUMENT_TYPE,
                DRAFT_STATUS,
                totalDamageAmount,
                request.getFaultRatio(),
                createdAt
        );

        return new PaymentApprovalDraftResponse(
                normalizedAccidentNumber,
                totalDamageAmount,
                request.getFaultRatio(),
                calculatedPaymentAmount,
                request.getMedicalExpense(),
                request.getLostIncome(),
                request.getRepairCost(),
                request.getSettlementAmount(),
                "입력된 손해액 정보를 바탕으로 지급품의서 초안을 작성했습니다."
        );
    }

    @Transactional
    public PaymentApprovalDocumentResponse saveAdjusterOpinion(
            String accidentNumber,
            AdjusterOpinionRequest request
    ) {
        String normalizedAccidentNumber = requireMatchingAccidentNumber(accidentNumber, request.getAccidentNumber());
        requireAccidentReport(normalizedAccidentNumber);
        rejectAlreadyApprovalRequested(normalizedAccidentNumber);

        int updated = damageInvestigationMapper.updateLatestPaymentApprovalOpinion(
                normalizedAccidentNumber,
                request.getFaultRatioOpinion().trim(),
                request.getAdjusterOpinion().trim(),
                OPINION_SAVED_STATUS
        );
        if (updated == 0) {
            throw new NoSuchElementException("Payment approval draft not found: " + normalizedAccidentNumber);
        }

        PaymentApprovalDocumentResponse response =
                damageInvestigationMapper.findPaymentApprovalDocumentByAccidentNumber(normalizedAccidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Payment approval document not found: " + normalizedAccidentNumber);
        }
        return response;
    }

    @Transactional
    public PaymentApprovalDocumentResponse approveInvestigation(
            String accidentNumber,
            InvestigationApprovalRequest request
    ) {
        String normalizedAccidentNumber = requireMatchingAccidentNumber(accidentNumber, request.getAccidentNumber());
        requireAccidentReport(normalizedAccidentNumber);
        rejectAlreadyApprovalRequested(normalizedAccidentNumber);

        LocalDateTime submittedAt = LocalDateTime.now();
        int submitted = damageInvestigationMapper.submitLatestPaymentApprovalDocument(
                normalizedAccidentNumber,
                request.getEmployeeNo().trim(),
                APPROVAL_REQUESTED_STATUS,
                submittedAt
        );
        if (submitted == 0) {
            throw new NoSuchElementException("Payment approval document not found: " + normalizedAccidentNumber);
        }

        damageInvestigationMapper.updateAccidentStatusToApprovalRequired(normalizedAccidentNumber);
        PaymentApprovalDocumentResponse response =
                damageInvestigationMapper.findPaymentApprovalDocumentByAccidentNumber(normalizedAccidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Payment approval document not found: " + normalizedAccidentNumber);
        }
        return response;
    }

    private PaymentApprovalDocumentResponse requirePaymentApprovalDocument(String accidentNumber) {
        PaymentApprovalDocumentResponse response =
                damageInvestigationMapper.findPaymentApprovalDocumentByAccidentNumber(accidentNumber);
        if (response == null) {
            throw new NoSuchElementException("Payment approval document not found: " + accidentNumber);
        }
        return response;
    }

    private void updatePaymentApprovalStatus(
            String accidentNumber,
            String currentStatus,
            String submissionStatus,
            String invalidTransitionMessage
    ) {
        PaymentApprovalDocumentResponse existing = requirePaymentApprovalDocument(accidentNumber);
        if (!currentStatus.equals(existing.getSubmissionStatus())) {
            throw new IllegalArgumentException(invalidTransitionMessage);
        }

        int updated = damageInvestigationMapper.updateLatestPaymentApprovalStatus(
                accidentNumber,
                currentStatus,
                submissionStatus
        );
        if (updated == 0) {
            throw new IllegalArgumentException(invalidTransitionMessage);
        }
    }

    private AccidentReport requireAccidentReport(String accidentNumber) {
        String normalizedAccidentNumber = requireText(accidentNumber, "accidentNumber");
        AccidentReport report = damageInvestigationMapper.findAccidentReportForInvestigation(normalizedAccidentNumber);
        if (report == null) {
            throw new NoSuchElementException("Accident report not found: " + normalizedAccidentNumber);
        }
        return report;
    }

    private String requireMatchingAccidentNumber(String pathAccidentNumber, String bodyAccidentNumber) {
        String normalizedPathAccidentNumber = requireText(pathAccidentNumber, "accidentNumber");
        String normalizedBodyAccidentNumber = requireText(bodyAccidentNumber, "accidentNumber");
        if (!normalizedPathAccidentNumber.equals(normalizedBodyAccidentNumber)) {
            throw new IllegalArgumentException("Path accidentNumber and request accidentNumber must match.");
        }
        return normalizedPathAccidentNumber;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private BigDecimal calculateTotalDamageAmount(DamageAssessmentRequest request) {
        return request.getMedicalExpense()
                .add(request.getLostIncome())
                .add(request.getRepairCost())
                .add(request.getSettlementAmount());
    }

    private BigDecimal calculatePaymentAmount(BigDecimal totalDamageAmount, Float faultRatio) {
        return totalDamageAmount
                .multiply(BigDecimal.valueOf(faultRatio))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private void rejectDuplicatePaymentApprovalDocument(String accidentNumber) {
        DamageInvestigationResultResponse existingResult =
                damageInvestigationMapper.findDamageInvestigationResultByAccidentNumber(accidentNumber);
        if (existingResult != null) {
            throw new IllegalArgumentException("Damage investigation result already exists: " + accidentNumber);
        }

        PaymentApprovalDocumentResponse existing =
                damageInvestigationMapper.findPaymentApprovalDocumentByAccidentNumber(accidentNumber);
        if (existing != null) {
            throw new IllegalArgumentException("Payment approval document already exists: " + accidentNumber);
        }
    }

    private void rejectAlreadyApprovalRequested(String accidentNumber) {
        PaymentApprovalDocumentResponse existing =
                damageInvestigationMapper.findPaymentApprovalDocumentByAccidentNumber(accidentNumber);
        if (existing != null && APPROVAL_REQUESTED_STATUS.equals(existing.getSubmissionStatus())) {
            throw new IllegalArgumentException("Damage investigation is already approval requested: " + accidentNumber);
        }
    }

    private String generateId(String prefix) {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return prefix + "-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }
}
