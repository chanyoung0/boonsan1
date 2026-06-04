package com.boonsan.domain.contract.controller;

import com.boonsan.global.response.ApiResponse;
import com.boonsan.domain.contract.dto.EndorsementCreateRequest;
import com.boonsan.domain.contract.dto.EndorsementResponse;
import com.boonsan.domain.contract.dto.UnderwritingRequestCompleteRequest;
import com.boonsan.domain.contract.dto.UnderwritingRequestCreateRequest;
import com.boonsan.domain.contract.dto.UnderwritingRequestResponse;
import com.boonsan.domain.contract.service.EndorsementApplicationService;
import com.boonsan.domain.contract.service.UnderwritingRequestApplicationService;
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
@RequestMapping("/api/contracts/{policyNumber}/endorsements")
public class EndorsementController {

    private final EndorsementApplicationService endorsementApplicationService;
    private final UnderwritingRequestApplicationService underwritingRequestApplicationService;

    public EndorsementController(
            EndorsementApplicationService endorsementApplicationService,
            UnderwritingRequestApplicationService underwritingRequestApplicationService
    ) {
        this.endorsementApplicationService = endorsementApplicationService;
        this.underwritingRequestApplicationService = underwritingRequestApplicationService;
    }

    @PostMapping
    public ApiResponse<EndorsementResponse> apply(
            @PathVariable String policyNumber,
            @Valid @RequestBody EndorsementCreateRequest request
    ) {
        EndorsementResponse response = endorsementApplicationService.apply(policyNumber, request);
        return ApiResponse.success(response, "Endorsement applied");
    }

    @GetMapping("/current")
    public ApiResponse<EndorsementResponse> getCurrent(@PathVariable String policyNumber) {
        EndorsementResponse response = endorsementApplicationService.getActive(policyNumber);
        return ApiResponse.success(response, "Active endorsement found");
    }

    @GetMapping
    public ApiResponse<List<EndorsementResponse>> list(@PathVariable String policyNumber) {
        List<EndorsementResponse> response = endorsementApplicationService.listByPolicyNumber(policyNumber);
        return ApiResponse.success(response, "Endorsements found");
    }

    @PostMapping("/current/underwriting-request")
    public ApiResponse<UnderwritingRequestResponse> requestUnderwriting(
            @PathVariable String policyNumber,
            @RequestBody(required = false) UnderwritingRequestCreateRequest request
    ) {
        UnderwritingRequestResponse response =
                endorsementApplicationService.requestUnderwriting(policyNumber, request);
        return ApiResponse.success(response, "Underwriting requested");
    }

    @PatchMapping("/current/underwriting-request/complete")
    public ApiResponse<UnderwritingRequestResponse> completeUnderwriting(
            @PathVariable String policyNumber,
            @Valid @RequestBody UnderwritingRequestCompleteRequest request
    ) {
        UnderwritingRequestResponse response =
                endorsementApplicationService.completeUnderwriting(policyNumber, request);
        return ApiResponse.success(response, "Underwriting completed");
    }

    @GetMapping("/current/underwriting-request")
    public ApiResponse<UnderwritingRequestResponse> getUnderwriting(
            @PathVariable String policyNumber
    ) {
        EndorsementResponse active = endorsementApplicationService.getActive(policyNumber);
        if (active.getUnderwritingRequestId() == null) {
            throw new java.util.NoSuchElementException(
                    "No underwriting request linked to endorsement: " + active.getEndorsementId());
        }
        UnderwritingRequestResponse response =
                underwritingRequestApplicationService.findById(active.getUnderwritingRequestId());
        return ApiResponse.success(response, "Underwriting request found");
    }

    @PatchMapping("/current/approve")
    public ApiResponse<EndorsementResponse> approve(@PathVariable String policyNumber) {
        EndorsementResponse response = endorsementApplicationService.approve(policyNumber);
        return ApiResponse.success(response, "Endorsement approved");
    }

    @PatchMapping("/current/reject")
    public ApiResponse<EndorsementResponse> reject(@PathVariable String policyNumber) {
        EndorsementResponse response = endorsementApplicationService.reject(policyNumber);
        return ApiResponse.success(response, "Endorsement rejected");
    }

    @PatchMapping("/current/cancel")
    public ApiResponse<EndorsementResponse> cancel(@PathVariable String policyNumber) {
        EndorsementResponse response = endorsementApplicationService.cancel(policyNumber);
        return ApiResponse.success(response, "Endorsement cancelled");
    }
}
