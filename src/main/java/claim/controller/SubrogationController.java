package claim.controller;

import claim.dto.SubrogationCompleteRequest;
import claim.dto.SubrogationCreateRequest;
import claim.dto.SubrogationEligibilityResponse;
import claim.dto.SubrogationResponse;
import claim.service.SubrogationApplicationService;
import common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims/accident-reports/{accidentNumber}/subrogation")
public class SubrogationController {

    private final SubrogationApplicationService subrogationApplicationService;

    public SubrogationController(SubrogationApplicationService subrogationApplicationService) {
        this.subrogationApplicationService = subrogationApplicationService;
    }

    @GetMapping("/eligibility")
    public ApiResponse<SubrogationEligibilityResponse> getEligibility(@PathVariable String accidentNumber) {
        SubrogationEligibilityResponse response = subrogationApplicationService.getEligibility(accidentNumber);
        return ApiResponse.success(response, "Subrogation eligibility loaded");
    }

    @GetMapping
    public ApiResponse<SubrogationResponse> getSubrogation(@PathVariable String accidentNumber) {
        SubrogationResponse response = subrogationApplicationService.getSubrogation(accidentNumber);
        return ApiResponse.success(response, "Subrogation request found");
    }

    @PostMapping
    public ApiResponse<SubrogationResponse> createSubrogation(
            @PathVariable String accidentNumber,
            @Valid @RequestBody SubrogationCreateRequest request
    ) {
        SubrogationResponse response = subrogationApplicationService.createSubrogation(accidentNumber, request);
        return ApiResponse.success(response, "Subrogation request registered");
    }

    @PatchMapping("/complete")
    public ApiResponse<SubrogationResponse> completeSubrogation(
            @PathVariable String accidentNumber,
            @Valid @RequestBody SubrogationCompleteRequest request
    ) {
        SubrogationResponse response = subrogationApplicationService.completeSubrogation(accidentNumber, request);
        return ApiResponse.success(response, "Subrogation recovery completed");
    }
}
