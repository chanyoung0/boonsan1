package com.boonsan.claim.controller;

import com.boonsan.claim.dto.AdjusterOpinionRequest;
import com.boonsan.claim.dto.DamageAssessmentRequest;
import com.boonsan.claim.dto.DamageInvestigationResultResponse;
import com.boonsan.claim.dto.DamageInvestigationStartResponse;
import com.boonsan.claim.dto.FieldInvestigationMaterialResponse;
import com.boonsan.claim.dto.InvestigationApprovalRequest;
import com.boonsan.claim.dto.PaymentApprovalDocumentResponse;
import com.boonsan.claim.dto.PaymentApprovalDraftResponse;
import com.boonsan.claim.service.DamageInvestigationApplicationService;
import com.boonsan.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
