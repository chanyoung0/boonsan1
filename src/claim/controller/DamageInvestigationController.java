package claim.controller;

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
import claim.service.DamageInvestigationApplicationService;
import common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/claims/accident-reports/{accidentNumber}")
public class DamageInvestigationController {

    private final DamageInvestigationApplicationService damageInvestigationApplicationService;

    public DamageInvestigationController(
            DamageInvestigationApplicationService damageInvestigationApplicationService
    ) {
        this.damageInvestigationApplicationService = damageInvestigationApplicationService;
    }

    @GetMapping("/investigation")
    public ApiResponse<DamageInvestigationStartResponse> getAccidentReportForInvestigation(
            @PathVariable String accidentNumber
    ) {
        DamageInvestigationStartResponse response =
                damageInvestigationApplicationService.getAccidentReportForInvestigation(accidentNumber);
        return ApiResponse.success(response, "Accident report loaded for damage investigation");
    }

    @GetMapping("/field-materials")
    public ApiResponse<FieldInvestigationMaterialResponse> getFieldInvestigationMaterials(
            @PathVariable String accidentNumber
    ) {
        FieldInvestigationMaterialResponse response =
                damageInvestigationApplicationService.getFieldInvestigationMaterials(accidentNumber);
        return ApiResponse.success(response, "Field investigation materials loaded");
    }

    @GetMapping("/damage-investigations/result")
    public ApiResponse<DamageInvestigationResultResponse> getDamageInvestigationResult(
            @PathVariable String accidentNumber
    ) {
        DamageInvestigationResultResponse response =
                damageInvestigationApplicationService.getDamageInvestigationResult(accidentNumber);
        return ApiResponse.success(response, "Damage investigation result found");
    }

    @GetMapping("/payment-approval-document")
    public ApiResponse<PaymentApprovalDocumentResponse> getPaymentApprovalDocument(
            @PathVariable String accidentNumber
    ) {
        PaymentApprovalDocumentResponse response =
                damageInvestigationApplicationService.getPaymentApprovalDocument(accidentNumber);
        return ApiResponse.success(response, "Payment approval document found");
    }

    @PatchMapping("/payment-approval-document/approve")
    public ApiResponse<PaymentApprovalDocumentResponse> approvePaymentApprovalDocument(
            @PathVariable String accidentNumber
    ) {
        PaymentApprovalDocumentResponse response =
                damageInvestigationApplicationService.approvePaymentApprovalDocument(accidentNumber);
        return ApiResponse.success(response, "Payment approval document approved");
    }

    @PatchMapping("/payment-approval-document/reject")
    public ApiResponse<PaymentApprovalDocumentResponse> rejectPaymentApprovalDocument(
            @PathVariable String accidentNumber
    ) {
        PaymentApprovalDocumentResponse response =
                damageInvestigationApplicationService.rejectPaymentApprovalDocument(accidentNumber);
        return ApiResponse.success(response, "Payment approval document rejected");
    }

    @PatchMapping("/payment-approval-document/pay")
    public ApiResponse<PaymentApprovalDocumentResponse> payPaymentApprovalDocument(
            @PathVariable String accidentNumber
    ) {
        PaymentApprovalDocumentResponse response =
                damageInvestigationApplicationService.payPaymentApprovalDocument(accidentNumber);
        return ApiResponse.success(response, "Insurance payment completed");
    }

    @PostMapping("/investigation/reject")
    public ApiResponse<ClaimAlternativeFlowResponse> rejectInsuranceProcessing(
            @PathVariable String accidentNumber,
            @Valid @RequestBody DamageInvestigationRejectRequest request
    ) {
        return ApiResponse.success(
                damageInvestigationApplicationService.rejectInsuranceProcessing(accidentNumber, request),
                "Insurance processing rejected"
        );
    }

    @PostMapping("/investigation/fraud-request")
    public ApiResponse<ClaimAlternativeFlowResponse> requestFraudInvestigation(
            @PathVariable String accidentNumber,
            @Valid @RequestBody FraudInvestigationRequest request
    ) {
        return ApiResponse.success(
                damageInvestigationApplicationService.requestFraudInvestigation(accidentNumber, request),
                "Mock SIU investigation requested"
        );
    }

    @PostMapping("/investigation/outsource")
    public ApiResponse<ClaimAlternativeFlowResponse> requestOutsourceInvestigation(
            @PathVariable String accidentNumber,
            @Valid @RequestBody OutsourceInvestigationRequest request
    ) {
        return ApiResponse.success(
                damageInvestigationApplicationService.requestOutsourceInvestigation(accidentNumber, request),
                "Outsource investigation requested"
        );
    }

    @PatchMapping("/investigation/outsource/complete")
    public ApiResponse<ClaimAlternativeFlowResponse> completeOutsourceInvestigation(
            @PathVariable String accidentNumber
    ) {
        return ApiResponse.success(
                damageInvestigationApplicationService.completeOutsourceInvestigation(accidentNumber),
                "Mock outsource investigation completed"
        );
    }

    @GetMapping("/investigation/actions")
    public ApiResponse<List<ClaimAlternativeFlowResponse>> getAlternativeFlowHistory(
            @PathVariable String accidentNumber
    ) {
        return ApiResponse.success(
                damageInvestigationApplicationService.getAlternativeFlowHistory(accidentNumber),
                "Alternative flow history loaded"
        );
    }

    @PostMapping("/damage-investigations/draft")
    public ApiResponse<PaymentApprovalDraftResponse> createPaymentApprovalDraft(
            @PathVariable String accidentNumber,
            @Valid @RequestBody DamageAssessmentRequest request
    ) {
        PaymentApprovalDraftResponse response =
                damageInvestigationApplicationService.createPaymentApprovalDraft(accidentNumber, request);
        return ApiResponse.success(response, "Payment approval draft created");
    }

    @PostMapping("/damage-investigations/opinion")
    public ApiResponse<PaymentApprovalDocumentResponse> saveAdjusterOpinion(
            @PathVariable String accidentNumber,
            @Valid @RequestBody AdjusterOpinionRequest request
    ) {
        PaymentApprovalDocumentResponse response =
                damageInvestigationApplicationService.saveAdjusterOpinion(accidentNumber, request);
        return ApiResponse.success(response, "Adjuster opinion saved");
    }

    @PostMapping("/damage-investigations/approval-request")
    public ApiResponse<PaymentApprovalDocumentResponse> requestApproval(
            @PathVariable String accidentNumber,
            @Valid @RequestBody InvestigationApprovalRequest request
    ) {
        PaymentApprovalDocumentResponse response =
                damageInvestigationApplicationService.approveInvestigation(accidentNumber, request);
        return ApiResponse.success(response, "Damage investigation approval requested");
    }
}
