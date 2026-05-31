package claim.service;

import claim.dto.AdjusterOpinionRequest;
import claim.dto.DamageAssessmentRequest;
import claim.dto.DamageInvestigationStartResponse;
import claim.dto.FieldInvestigationMaterialResponse;
import claim.dto.InvestigationApprovalRequest;
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
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DamageInvestigationApplicationService {

    private static final String PAYMENT_APPROVAL_DOCUMENT_TYPE = "PAYMENT_APPROVAL";
    private static final String DRAFT_STATUS = "DRAFT";
    private static final String OPINION_SAVED_STATUS = "OPINION_SAVED";
    private static final String APPROVAL_REQUESTED_STATUS = "APPROVAL_REQUESTED";

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

    @Transactional
    public PaymentApprovalDraftResponse createPaymentApprovalDraft(
            String accidentNumber,
            DamageAssessmentRequest request
    ) {
        String normalizedAccidentNumber = requireMatchingAccidentNumber(accidentNumber, request.getAccidentNumber());
        requireAccidentReport(normalizedAccidentNumber);
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

    private String generateId(String prefix) {
        int sequence = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        return prefix + "-" + Year.now().getValue() + "-" + String.format("%06d", sequence);
    }
}
