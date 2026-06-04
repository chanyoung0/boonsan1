package com.boonsan.underwriting.controller;

import com.boonsan.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.boonsan.underwriting.dto.CoinsuranceCreateRequest;
import com.boonsan.underwriting.dto.CoinsuranceProcessResponse;
import com.boonsan.underwriting.dto.CoinsuranceResultRequest;
import com.boonsan.underwriting.dto.PolicyIssueResponse;
import com.boonsan.underwriting.dto.ReinsuranceCreateRequest;
import com.boonsan.underwriting.dto.ReinsuranceProcessResponse;
import com.boonsan.underwriting.dto.ReinsuranceResultRequest;
import com.boonsan.underwriting.dto.UnderwritingFollowUpEligibilityResponse;
import com.boonsan.underwriting.service.UnderwritingFollowUpApplicationService;

@RestController
@RequestMapping("/api/underwriting/applications/{applicationId}")
public class UnderwritingFollowUpController {

    private final UnderwritingFollowUpApplicationService followUpApplicationService;

    public UnderwritingFollowUpController(UnderwritingFollowUpApplicationService followUpApplicationService) {
        this.followUpApplicationService = followUpApplicationService;
    }

    @GetMapping("/coinsurance/eligibility")
    public ApiResponse<UnderwritingFollowUpEligibilityResponse> getCoinsuranceEligibility(
            @PathVariable String applicationId
    ) {
        return ApiResponse.success(
                followUpApplicationService.getCoinsuranceEligibility(applicationId),
                "Coinsurance eligibility loaded"
        );
    }

    @PostMapping("/coinsurance")
    public ApiResponse<CoinsuranceProcessResponse> createCoinsurance(
            @PathVariable String applicationId,
            @Valid @RequestBody CoinsuranceCreateRequest request
    ) {
        return ApiResponse.success(
                followUpApplicationService.createCoinsurance(applicationId, request),
                "Coinsurance process created"
        );
    }

    @GetMapping("/coinsurance")
    public ApiResponse<CoinsuranceProcessResponse> getCoinsurance(@PathVariable String applicationId) {
        return ApiResponse.success(
                followUpApplicationService.getCoinsurance(applicationId),
                "Coinsurance process loaded"
        );
    }

    @PatchMapping("/coinsurance/result")
    public ApiResponse<CoinsuranceProcessResponse> updateCoinsuranceResult(
            @PathVariable String applicationId,
            @Valid @RequestBody CoinsuranceResultRequest request
    ) {
        return ApiResponse.success(
                followUpApplicationService.updateCoinsuranceResult(applicationId, request),
                "Coinsurance result saved"
        );
    }

    @GetMapping("/reinsurance/eligibility")
    public ApiResponse<UnderwritingFollowUpEligibilityResponse> getReinsuranceEligibility(
            @PathVariable String applicationId
    ) {
        return ApiResponse.success(
                followUpApplicationService.getReinsuranceEligibility(applicationId),
                "Reinsurance eligibility loaded"
        );
    }

    @PostMapping("/reinsurance")
    public ApiResponse<ReinsuranceProcessResponse> createReinsurance(
            @PathVariable String applicationId,
            @Valid @RequestBody ReinsuranceCreateRequest request
    ) {
        return ApiResponse.success(
                followUpApplicationService.createReinsurance(applicationId, request),
                "Reinsurance process created"
        );
    }

    @GetMapping("/reinsurance")
    public ApiResponse<ReinsuranceProcessResponse> getReinsurance(@PathVariable String applicationId) {
        return ApiResponse.success(
                followUpApplicationService.getReinsurance(applicationId),
                "Reinsurance process loaded"
        );
    }

    @PatchMapping("/reinsurance/result")
    public ApiResponse<ReinsuranceProcessResponse> updateReinsuranceResult(
            @PathVariable String applicationId,
            @Valid @RequestBody ReinsuranceResultRequest request
    ) {
        return ApiResponse.success(
                followUpApplicationService.updateReinsuranceResult(applicationId, request),
                "Reinsurance result saved"
        );
    }

    @GetMapping("/policy-issue/eligibility")
    public ApiResponse<UnderwritingFollowUpEligibilityResponse> getPolicyIssueEligibility(
            @PathVariable String applicationId
    ) {
        return ApiResponse.success(
                followUpApplicationService.getPolicyIssueEligibility(applicationId),
                "Policy issue eligibility loaded"
        );
    }

    @PostMapping("/policy-issue")
    public ApiResponse<PolicyIssueResponse> issuePolicy(@PathVariable String applicationId) {
        return ApiResponse.success(
                followUpApplicationService.issuePolicy(applicationId),
                "Policy issued"
        );
    }

    @GetMapping("/policy-issue")
    public ApiResponse<PolicyIssueResponse> getPolicyIssue(@PathVariable String applicationId) {
        return ApiResponse.success(
                followUpApplicationService.getPolicyIssue(applicationId),
                "Policy issue loaded"
        );
    }
}
