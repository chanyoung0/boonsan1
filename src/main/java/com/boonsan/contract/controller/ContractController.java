package com.boonsan.contract.controller;

import com.boonsan.common.ApiResponse;
import com.boonsan.contract.dto.ContractResponse;
import com.boonsan.contract.service.ContractApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractApplicationService contractApplicationService;

    public ContractController(ContractApplicationService contractApplicationService) {
        this.contractApplicationService = contractApplicationService;
    }

    @GetMapping("/{policyNumber}")
    public ApiResponse<ContractResponse> findByPolicyNumber(@PathVariable String policyNumber) {
        ContractResponse response = contractApplicationService.findByPolicyNumber(policyNumber);
        return ApiResponse.success(response, "Contract found");
    }
}
