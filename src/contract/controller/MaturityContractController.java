package contract.controller;

import common.ApiResponse;
import contract.dto.MaturityNoticeResponse;
import contract.dto.MaturityProcessResponse;
import contract.dto.MaturityRenewalIntentionRequest;
import contract.dto.MaturityRenewalResponse;
import contract.service.MaturityContractApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts/{policyNumber}/maturity")
public class MaturityContractController {

    private final MaturityContractApplicationService maturityContractApplicationService;

    public MaturityContractController(MaturityContractApplicationService maturityContractApplicationService) {
        this.maturityContractApplicationService = maturityContractApplicationService;
    }

    @GetMapping("/notice")
    public ApiResponse<MaturityNoticeResponse> getNotice(@PathVariable String policyNumber) {
        MaturityNoticeResponse response = maturityContractApplicationService.getMaturityNotice(policyNumber);
        return ApiResponse.success(response, "Maturity notice generated");
    }

    @PostMapping("/notice")
    public ApiResponse<MaturityNoticeResponse> sendNotice(@PathVariable String policyNumber) {
        MaturityNoticeResponse response = maturityContractApplicationService.sendMaturityNotice(policyNumber);
        return ApiResponse.success(response, "Maturity notice sent");
    }

    @PatchMapping("/renewal-intention")
    public ApiResponse<MaturityRenewalResponse> recordRenewalIntention(
            @PathVariable String policyNumber,
            @Valid @RequestBody MaturityRenewalIntentionRequest request
    ) {
        MaturityRenewalResponse response =
                maturityContractApplicationService.recordRenewalIntention(policyNumber, request);
        return ApiResponse.success(response, "Renewal intention recorded");
    }

    @PostMapping
    public ApiResponse<MaturityProcessResponse> process(@PathVariable String policyNumber) {
        MaturityProcessResponse response = maturityContractApplicationService.processMaturity(policyNumber);
        return ApiResponse.success(response, "Maturity processed");
    }
}
