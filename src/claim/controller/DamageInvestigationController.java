package claim.controller;

import claim.dto.AdjusterOpinionRequest;
import claim.dto.DamageAssessmentRequest;
import claim.dto.DamageInvestigationResultResponse;
import claim.dto.DamageInvestigationStartResponse;
import claim.dto.FieldInvestigationMaterialResponse;
import claim.dto.InvestigationApprovalRequest;
import claim.dto.PaymentApprovalDocumentResponse;
import claim.dto.PaymentApprovalDraftResponse;
import claim.service.DamageInvestigationApplicationService;
import common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
