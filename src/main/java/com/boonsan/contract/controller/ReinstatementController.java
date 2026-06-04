package com.boonsan.contract.controller;

import com.boonsan.common.ApiResponse;
import com.boonsan.contract.dto.ReinstatementCreateRequest;
import com.boonsan.contract.dto.ReinstatementResponse;
import com.boonsan.contract.dto.UnderwritingRequestCompleteRequest;
import com.boonsan.contract.dto.UnderwritingRequestCreateRequest;
import com.boonsan.contract.dto.UnderwritingRequestResponse;
import com.boonsan.contract.service.ReinstatementApplicationService;
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
@RequestMapping("/api/contracts/{policyNumber}/reinstatements")
public class ReinstatementController {

    private final ReinstatementApplicationService reinstatementApplicationService;

    public ReinstatementController(ReinstatementApplicationService reinstatementApplicationService) {
        this.reinstatementApplicationService = reinstatementApplicationService;
    }

    @PostMapping
    public ApiResponse<ReinstatementResponse> apply(
            @PathVariable String policyNumber,
            @Valid @RequestBody ReinstatementCreateRequest request
    ) {
        ReinstatementResponse response = reinstatementApplicationService.apply(policyNumber, request);
        return ApiResponse.success(response, "Reinstatement applied");
    }

    @GetMapping("/current")
    public ApiResponse<ReinstatementResponse> getCurrent(@PathVariable String policyNumber) {
        ReinstatementResponse response = reinstatementApplicationService.getActive(policyNumber);
        return ApiResponse.success(response, "Active reinstatement found");
    }

    @GetMapping
    public ApiResponse<List<ReinstatementResponse>> list(@PathVariable String policyNumber) {
        List<ReinstatementResponse> response = reinstatementApplicationService.listByPolicyNumber(policyNumber);
        return ApiResponse.success(response, "Reinstatements found");
    }

    @PatchMapping("/current/settle-unpaid")
    public ApiResponse<ReinstatementResponse> settleUnpaid(@PathVariable String policyNumber) {
        ReinstatementResponse response = reinstatementApplicationService.settleUnpaid(policyNumber);
        return ApiResponse.success(response, "Unpaid premium settled");
    }

    @PatchMapping("/current/complete")
    public ApiResponse<ReinstatementResponse> complete(@PathVariable String policyNumber) {
        ReinstatementResponse response = reinstatementApplicationService.complete(policyNumber);
        return ApiResponse.success(response, "Reinstatement completed");
    }

    @PatchMapping("/current/cancel")
    public ApiResponse<ReinstatementResponse> cancel(@PathVariable String policyNumber) {
        ReinstatementResponse response = reinstatementApplicationService.cancel(policyNumber);
        return ApiResponse.success(response, "Reinstatement cancelled");
    }

    @PostMapping("/current/underwriting-request")
    public ApiResponse<UnderwritingRequestResponse> requestUnderwriting(
            @PathVariable String policyNumber,
            @RequestBody(required = false) UnderwritingRequestCreateRequest request
    ) {
        UnderwritingRequestResponse response =
                reinstatementApplicationService.requestUnderwriting(policyNumber, request);
        return ApiResponse.success(response, "Underwriting requested");
    }

    @PatchMapping("/current/underwriting-request/complete")
    public ApiResponse<UnderwritingRequestResponse> completeUnderwriting(
            @PathVariable String policyNumber,
            @Valid @RequestBody UnderwritingRequestCompleteRequest request
    ) {
        UnderwritingRequestResponse response =
                reinstatementApplicationService.completeUnderwriting(policyNumber, request);
        return ApiResponse.success(response, "Underwriting completed");
    }

    @GetMapping("/current/underwriting-request")
    public ApiResponse<UnderwritingRequestResponse> getUnderwritingRequest(@PathVariable String policyNumber) {
        UnderwritingRequestResponse response = reinstatementApplicationService.getUnderwritingRequest(policyNumber);
        return ApiResponse.success(response, "Underwriting request found");
    }
}
