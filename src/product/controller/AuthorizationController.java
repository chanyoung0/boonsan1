package product.controller;

import common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import product.dto.AuthorizationCreateRequest;
import product.dto.AuthorizationEligibilityResponse;
import product.dto.AuthorizationResponse;
import product.dto.AuthorizationRevisionRequest;
import product.service.AuthorizationApplicationService;

@RestController
@RequestMapping("/api/products/{productCode}/authorization")
public class AuthorizationController {

    private final AuthorizationApplicationService authorizationApplicationService;

    public AuthorizationController(AuthorizationApplicationService authorizationApplicationService) {
        this.authorizationApplicationService = authorizationApplicationService;
    }

    @GetMapping("/eligibility")
    public ApiResponse<AuthorizationEligibilityResponse> getEligibility(@PathVariable String productCode) {
        AuthorizationEligibilityResponse response = authorizationApplicationService.getEligibility(productCode);
        return ApiResponse.success(response, "Eligibility checked");
    }

    @GetMapping
    public ApiResponse<AuthorizationResponse> getLatest(@PathVariable String productCode) {
        AuthorizationResponse response = authorizationApplicationService.getLatest(productCode);
        return ApiResponse.success(response, "Authorization found");
    }

    @PostMapping
    public ApiResponse<AuthorizationResponse> create(
            @PathVariable String productCode,
            @Valid @RequestBody AuthorizationCreateRequest request
    ) {
        AuthorizationResponse response = authorizationApplicationService.create(productCode, request);
        return ApiResponse.success(response, "Authorization requested");
    }

    @PatchMapping("/approve")
    public ApiResponse<AuthorizationResponse> approve(@PathVariable String productCode) {
        AuthorizationResponse response = authorizationApplicationService.approve(productCode);
        return ApiResponse.success(response, "Authorization approved");
    }

    @PatchMapping("/reject")
    public ApiResponse<AuthorizationResponse> reject(@PathVariable String productCode) {
        AuthorizationResponse response = authorizationApplicationService.reject(productCode);
        return ApiResponse.success(response, "Authorization rejected");
    }

    @PatchMapping("/revision")
    public ApiResponse<AuthorizationResponse> requestRevision(
            @PathVariable String productCode,
            @Valid @RequestBody AuthorizationRevisionRequest request
    ) {
        AuthorizationResponse response = authorizationApplicationService.requestRevision(productCode, request);
        return ApiResponse.success(response, "Revision requested");
    }

    @PatchMapping("/cancel")
    public ApiResponse<AuthorizationResponse> cancel(@PathVariable String productCode) {
        AuthorizationResponse response = authorizationApplicationService.cancel(productCode);
        return ApiResponse.success(response, "Authorization cancelled");
    }
}
