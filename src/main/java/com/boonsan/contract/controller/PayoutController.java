package com.boonsan.contract.controller;

import com.boonsan.common.ApiResponse;
import com.boonsan.contract.dto.PayoutApproveRequest;
import com.boonsan.contract.dto.PayoutCreateRequest;
import com.boonsan.contract.dto.PayoutResponse;
import com.boonsan.contract.service.PayoutApplicationService;
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
@RequestMapping("/api/contracts/{policyNumber}/payouts")
public class PayoutController {

    private final PayoutApplicationService payoutApplicationService;

    public PayoutController(PayoutApplicationService payoutApplicationService) {
        this.payoutApplicationService = payoutApplicationService;
    }

    @PostMapping
    public ApiResponse<PayoutResponse> create(
            @PathVariable String policyNumber,
            @Valid @RequestBody PayoutCreateRequest request
    ) {
        PayoutResponse response = payoutApplicationService.createPayout(policyNumber, request);
        return ApiResponse.success(response, "Payout calculated");
    }

    @GetMapping
    public ApiResponse<List<PayoutResponse>> list(@PathVariable String policyNumber) {
        List<PayoutResponse> response = payoutApplicationService.listByPolicyNumber(policyNumber);
        return ApiResponse.success(response, "Payouts found");
    }

    @GetMapping("/{payoutId}")
    public ApiResponse<PayoutResponse> findOne(
            @PathVariable String policyNumber,
            @PathVariable String payoutId
    ) {
        PayoutResponse response = payoutApplicationService.findByPayoutId(policyNumber, payoutId);
        return ApiResponse.success(response, "Payout found");
    }

    @PatchMapping("/{payoutId}/approve")
    public ApiResponse<PayoutResponse> approve(
            @PathVariable String policyNumber,
            @PathVariable String payoutId,
            @Valid @RequestBody PayoutApproveRequest request
    ) {
        PayoutResponse response = payoutApplicationService.approve(policyNumber, payoutId, request);
        return ApiResponse.success(response, "Payout approved");
    }

    @PatchMapping("/{payoutId}/pay")
    public ApiResponse<PayoutResponse> pay(
            @PathVariable String policyNumber,
            @PathVariable String payoutId
    ) {
        PayoutResponse response = payoutApplicationService.pay(policyNumber, payoutId);
        return ApiResponse.success(response, "Payout paid");
    }

    @PatchMapping("/{payoutId}/cancel")
    public ApiResponse<PayoutResponse> cancel(
            @PathVariable String policyNumber,
            @PathVariable String payoutId
    ) {
        PayoutResponse response = payoutApplicationService.cancel(policyNumber, payoutId);
        return ApiResponse.success(response, "Payout cancelled");
    }
}
