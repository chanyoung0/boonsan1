package contract.controller;

import common.ApiResponse;
import contract.dto.PaymentCollectionBatchRequest;
import contract.dto.PaymentCollectionBatchResponse;
import contract.dto.PaymentCollectionTargetResponse;
import contract.dto.PaymentCollectionTransferTargetResponse;
import contract.service.PaymentCollectionApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/payment-collections")
public class PaymentCollectionBatchController {

    private final PaymentCollectionApplicationService paymentCollectionApplicationService;

    public PaymentCollectionBatchController(PaymentCollectionApplicationService paymentCollectionApplicationService) {
        this.paymentCollectionApplicationService = paymentCollectionApplicationService;
    }

    @GetMapping("/targets")
    public ApiResponse<List<PaymentCollectionTargetResponse>> listTargets() {
        List<PaymentCollectionTargetResponse> response =
                paymentCollectionApplicationService.listCollectionTargets();
        return ApiResponse.success(response, "Payment collection targets found");
    }

    @PostMapping("/batch")
    public ApiResponse<PaymentCollectionBatchResponse> processBatch(
            @RequestBody(required = false) PaymentCollectionBatchRequest request
    ) {
        PaymentCollectionBatchResponse response = paymentCollectionApplicationService.processBatch(request);
        return ApiResponse.success(response, "Payment collection batch processed");
    }

    @GetMapping("/transfer-targets")
    public ApiResponse<List<PaymentCollectionTransferTargetResponse>> listTransferTargets() {
        List<PaymentCollectionTransferTargetResponse> response =
                paymentCollectionApplicationService.listTransferTargets();
        return ApiResponse.success(response, "Payment collection transfer targets found");
    }
}
