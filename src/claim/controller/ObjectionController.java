package claim.controller;

import claim.dto.ObjectionCreateRequest;
import claim.dto.ObjectionEligibilityResponse;
import claim.dto.ObjectionResponse;
import claim.service.ObjectionApplicationService;
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
@RequestMapping("/api/claims/accident-reports/{accidentNumber}/objection")
public class ObjectionController {

    private final ObjectionApplicationService objectionApplicationService;

    public ObjectionController(ObjectionApplicationService objectionApplicationService) {
        this.objectionApplicationService = objectionApplicationService;
    }

    @GetMapping("/eligibility")
    public ApiResponse<ObjectionEligibilityResponse> getEligibility(@PathVariable String accidentNumber) {
        ObjectionEligibilityResponse response = objectionApplicationService.getEligibility(accidentNumber);
        return ApiResponse.success(response, "Objection eligibility loaded");
    }

    @GetMapping
    public ApiResponse<ObjectionResponse> getObjection(@PathVariable String accidentNumber) {
        ObjectionResponse response = objectionApplicationService.getObjection(accidentNumber);
        return ApiResponse.success(response, "Objection found");
    }

    @PostMapping
    public ApiResponse<ObjectionResponse> createObjection(
            @PathVariable String accidentNumber,
            @Valid @RequestBody ObjectionCreateRequest request
    ) {
        ObjectionResponse response = objectionApplicationService.createObjection(accidentNumber, request);
        return ApiResponse.success(response, "Objection registered");
    }

    @PatchMapping("/reinvestigation")
    public ApiResponse<ObjectionResponse> markReinvestigationRequired(@PathVariable String accidentNumber) {
        ObjectionResponse response = objectionApplicationService.markReinvestigationRequired(accidentNumber);
        return ApiResponse.success(response, "Objection marked for reinvestigation");
    }

    @PatchMapping("/reject")
    public ApiResponse<ObjectionResponse> rejectObjection(@PathVariable String accidentNumber) {
        ObjectionResponse response = objectionApplicationService.rejectObjection(accidentNumber);
        return ApiResponse.success(response, "Objection rejected");
    }

    @PatchMapping("/legal-transfer")
    public ApiResponse<ObjectionResponse> transferToLegal(@PathVariable String accidentNumber) {
        ObjectionResponse response = objectionApplicationService.transferToLegal(accidentNumber);
        return ApiResponse.success(response, "Objection marked for legal transfer");
    }

    @PatchMapping("/complete")
    public ApiResponse<ObjectionResponse> completeObjection(@PathVariable String accidentNumber) {
        ObjectionResponse response = objectionApplicationService.completeObjection(accidentNumber);
        return ApiResponse.success(response, "Objection completed");
    }
}
