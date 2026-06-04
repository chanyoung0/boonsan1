package com.boonsan.domain.contract.controller;

import com.boonsan.global.response.ApiResponse;
import com.boonsan.domain.contract.dto.MaturityNoticeResponse;
import com.boonsan.domain.contract.dto.MaturityProcessResponse;
import com.boonsan.domain.contract.service.MaturityContractApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    public ApiResponse<MaturityProcessResponse> process(@PathVariable String policyNumber) {
        MaturityProcessResponse response = maturityContractApplicationService.processMaturity(policyNumber);
        return ApiResponse.success(response, "Maturity processed");
    }
}
