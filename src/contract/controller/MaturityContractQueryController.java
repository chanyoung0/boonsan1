package contract.controller;

import common.ApiResponse;
import contract.dto.MaturityTargetResponse;
import contract.service.MaturityContractApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/maturity")
public class MaturityContractQueryController {

    private final MaturityContractApplicationService maturityContractApplicationService;

    public MaturityContractQueryController(MaturityContractApplicationService maturityContractApplicationService) {
        this.maturityContractApplicationService = maturityContractApplicationService;
    }

    @GetMapping("/targets")
    public ApiResponse<List<MaturityTargetResponse>> listTargets() {
        return ApiResponse.success(
                maturityContractApplicationService.listMaturityTargets(),
                "Maturity targets retrieved"
        );
    }

    @GetMapping("/renewal-targets")
    public ApiResponse<List<MaturityTargetResponse>> listRenewalTargets() {
        return ApiResponse.success(
                maturityContractApplicationService.listRenewalTargets(),
                "Maturity renewal targets retrieved"
        );
    }
}
