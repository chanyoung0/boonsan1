package com.boonsan.contract.controller;

import com.boonsan.common.ApiResponse;
import com.boonsan.contract.dto.PaymentCollectionCreateRequest;
import com.boonsan.contract.dto.PaymentCollectionResponse;
import com.boonsan.contract.dto.PaymentCollectionTransferRequest;
import com.boonsan.contract.dto.UnpaidNoticeResponse;
import com.boonsan.contract.service.PaymentCollectionApplicationService;
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
@RequestMapping("/api/contracts/{policyNumber}/payment-collections")
public class PaymentCollectionController {

    private final PaymentCollectionApplicationService paymentCollectionApplicationService;

    public PaymentCollectionController(PaymentCollectionApplicationService paymentCollectionApplicationService) {
        this.paymentCollectionApplicationService = paymentCollectionApplicationService;
    }

    @PostMapping
    public ApiResponse<PaymentCollectionResponse> create(
            @PathVariable String policyNumber,
            @Valid @RequestBody PaymentCollectionCreateRequest request
    ) {
        PaymentCollectionResponse response =
                paymentCollectionApplicationService.createCollection(policyNumber, request);
        return ApiResponse.success(response, "Payment collection processed");
    }

    @GetMapping
    public ApiResponse<List<PaymentCollectionResponse>> list(@PathVariable String policyNumber) {
        List<PaymentCollectionResponse> response =
                paymentCollectionApplicationService.listByPolicyNumber(policyNumber);
        return ApiResponse.success(response, "Payment collections found");
    }

    @GetMapping("/{collectionId}")
    public ApiResponse<PaymentCollectionResponse> findOne(
            @PathVariable String policyNumber,
            @PathVariable String collectionId
    ) {
        PaymentCollectionResponse response =
                paymentCollectionApplicationService.findByCollectionId(policyNumber, collectionId);
        return ApiResponse.success(response, "Payment collection found");
    }

    @GetMapping("/{collectionId}/unpaid-notice")
    public ApiResponse<UnpaidNoticeResponse> getUnpaidNotice(
            @PathVariable String policyNumber,
            @PathVariable String collectionId
    ) {
        UnpaidNoticeResponse response =
                paymentCollectionApplicationService.getUnpaidNotice(policyNumber, collectionId);
        return ApiResponse.success(response, "Unpaid notice generated");
    }

    @PatchMapping("/{collectionId}/transfer")
    public ApiResponse<PaymentCollectionResponse> transfer(
            @PathVariable String policyNumber,
            @PathVariable String collectionId,
            @Valid @RequestBody PaymentCollectionTransferRequest request
    ) {
        PaymentCollectionResponse response =
                paymentCollectionApplicationService.transfer(policyNumber, collectionId, request);
        return ApiResponse.success(response, "Payment collection transferred");
    }
}
