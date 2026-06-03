package claim.service;

import claim.dto.AdjusterOpinionRequest;
import claim.dto.ClaimAlternativeFlowResponse;
import claim.dto.DamageAssessmentRequest;
import claim.dto.DamageInvestigationRejectRequest;
import claim.dto.DamageInvestigationResultResponse;
import claim.dto.DamageInvestigationStartResponse;
import claim.dto.FieldInvestigationMaterialResponse;
import claim.dto.InvestigationApprovalRequest;
import claim.dto.FraudInvestigationRequest;
import claim.dto.OutsourceInvestigationRequest;
import claim.dto.PaymentApprovalDocumentResponse;
import claim.dto.PaymentApprovalDraftResponse;
import claim.mapper.DamageInvestigationMapper;
import model.accident.AccidentReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DamageInvestigationApplicationService {

    private static final String PAYMENT_APPROVAL_DOCUMENT_TYPE = "PAYMENT_APPROVAL";
    private static final String DRAFT_STATUS = "DRAFT";
    private static final String OPINION_SAVED_STATUS = "OPINION_SAVED";
    private static final String APPROVAL_REQUESTED_STATUS = "APPROVAL_REQUESTED";
    private static final String APPROVED_STATUS = "APPROVED";
    private static final String REJECTED_STATUS = "REJECTED";
    private static final String PAID_STATUS = "PAID";
    private static final String COMPLETED_ACCIDENT_STATUS = "COMPLETED";
    private static final String REJECTED_ACCIDENT_STATUS = "REJECTED";
    private static final String FRAUD_INVESTIGATION_STATUS = "FRAUD_INVESTIGATION";
    private static final String OUTSOURCED_INVESTIGATION_STATUS = "OUTSOURCED_INVESTIGATION";
    private static final String FIELD_INVESTIGATION_REQUIRED_STATUS = "FIELD_INVESTIGATION_REQUIRED";
    private static final String ACTION_REJECTED = "INVESTIGATION_REJECTED";
    private static final String ACTION_FRAUD = "FRAUD_INVESTIGATION_REQUESTED";
    private static final String ACTION_OUTSOURCE = "OUTSOURCE_REQUESTED";

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
    public PaymentApprovalDocumentResponse payPaymentApprovalDocument(String accidentNumber) {
        String normalizedAccidentNumber = requireAccidentReport(accidentNumber).getReportNo();
        updatePaymentApprovalStatus(
                normalizedAccidentNumber,
                APPROVED_STATUS,
                PAID_STATUS,
                "Payment approval document can only be paid from APPROVED status."
        );
        damageInvestigationMapper.updateAccidentStatus(normalizedAccidentNumber, COMPLETED_ACCIDENT_STATUS);
        return requirePaymentApprovalDocument(normalizedAccidentNumber);
    }

    @Transactional
    public ClaimAlternativeFlowResponse rejectInsuranceProcessing(
            String accidentNumber,
            DamageInvestigationRejectRequest request
    ) {
        String normalized = requireAccidentReport(accidentNumber).getReportNo();
        LocalDateTime now = LocalDateTime.now();
        damageInvestigationMapper.insertAlternativeFlowHistory(
                generateId("CAF"), normalized, ACTION_REJECTED,
                requireText(request.getEmployeeNo(), "employeeNo"),
                requireText(request.getRejectionReason(), "rejectionReason"),
                null, null, "Insurance processing rejected", now
        );
        damageInvestigationMapper.updateAccidentStatus(normalized, REJECTED_ACCIDENT_STATUS);
        return latestAction(normalized);
    }

    @Transactional
    public ClaimAlternativeFlowResponse requestFraudInvestigation(
            String accidentNumber,
            FraudInvestigationRequest request
    ) {
        String normalized = requireAccidentReport(accidentNumber).getReportNo();
        if (!"실시한다".equals(request.getConfirmation().trim())) {
            throw new IllegalArgumentException("confirmation must be 실시한다.");
        }
        LocalDateTime now = LocalDateTime.now();
        damageInvestigationMapper.insertAlternativeFlowHistory(
                generateId("CAF"), normalized, ACTION_FRAUD,
                requireText(request.getEmployeeNo(), "employeeNo"),
                "Insurance fraud suspected", null, null,
                "Mock SIU delegation registered", now
        );
        damageInvestigationMapper.updateAccidentStatus(normalized, FRAUD_INVESTIGATION_STATUS);
        return latestAction(normalized);
    }

    @Transactional
    public ClaimAlternativeFlowResponse requestOutsourceInvestigation(
            String accidentNumber,
            OutsourceInvestigationRequest request
    ) {
        String normalized = requireAccidentReport(accidentNumber).getReportNo();
        LocalDateTime now = LocalDateTime.now();
        damageInvestigationMapper.insertAlternativeFlowHistory(
                generateId("CAF"), normalized, ACTION_OUTSOURCE,
                requireText(request.getEmployeeNo(), "employeeNo"),
                requireText(request.getRequestDetails(), "requestDetails"),
                requireText(request.getPartnerName(), "partnerName"),
                requireText(request.getMaterialChecklist(), "materialChecklist"),
                "Outsource request document stored", now
        );
        damageInvestigationMapper.updateAccidentStatus(normalized, OUTSOURCED_INVESTIGATION_STATUS);
        return latestAction(normalized);
    }

    @Transactional
    public ClaimAlternativeFlowResponse completeOutsourceInvestigation(String accidentNumber) {
        String normalized = requireAccidentReport(accidentNumber).getReportNo();
        int updated = damageInvestigationMapper.completeLatestAlternativeFlow(
                normalized, ACTION_OUTSOURCE, "Mock outsource result received", LocalDateTime.now()
        );
        if (updated == 0) {
            throw new IllegalArgumentException("Open outsource investigation request not found.");
        }
        damageInvestigationMapper.updateAccidentStatus(normalized, FIELD_INVESTIGATION_REQUIRED_STATUS);
        return latestAction(normalized);
    }

    @Transactional(readOnly = true)
    public List<ClaimAlternativeFlowResponse> getAlternativeFlowHistory(String accidentNumber) {
        String normalized = requireAccidentReport(accidentNumber).getReportNo();
        return damageInvestigationMapper.findAlternativeFlowHistory(normalized);
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
        PaymentApprovalDocumentResponse existing =
                damageInvestigationMapper.findPaymentApprovalDocumentByAccidentNumber(accidentNumber);
        if (existing != null && !REJECTED_STATUS.equals(existing.getSubmissionStatus())) {
            throw new IllegalArgumentException("Payment approval document already exists: " + accidentNumber);
        }
    }

    private ClaimAlternativeFlowResponse latestAction(String accidentNumber) {
        List<ClaimAlternativeFlowResponse> history = damageInvestigationMapper.findAlternativeFlowHistory(accidentNumber);
        if (history.isEmpty()) {
            throw new NoSuchElementException("Alternative flow history not found: " + accidentNumber);
        }
        return history.get(0);
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
