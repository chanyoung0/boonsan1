package com.boonsan.domain.underwriting.controller;

import com.boonsan.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.boonsan.domain.underwriting.dto.UnderwritingApplicationCreateRequest;
import com.boonsan.domain.underwriting.dto.UnderwritingApplicationResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingAutoScoreResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingFinalizeRequest;
import com.boonsan.domain.underwriting.dto.UnderwritingHistoryResponse;
import com.boonsan.domain.underwriting.dto.UnderwritingReviewResponse;
import com.boonsan.domain.underwriting.service.UnderwritingApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/underwriting/applications")
public class UnderwritingController {

    private final UnderwritingApplicationService underwritingApplicationService;

    public UnderwritingController(UnderwritingApplicationService underwritingApplicationService) {
        this.underwritingApplicationService = underwritingApplicationService;
    }

    @PostMapping
    public ApiResponse<UnderwritingApplicationResponse> createApplication(
            @Valid @RequestBody UnderwritingApplicationCreateRequest request
    ) {
        UnderwritingApplicationResponse response = underwritingApplicationService.createApplication(request);
        return ApiResponse.success(response, "Insurance application registered");
    }

    @GetMapping("/{applicationId}")
    public ApiResponse<UnderwritingApplicationResponse> getApplication(@PathVariable String applicationId) {
        UnderwritingApplicationResponse response = underwritingApplicationService.getApplication(applicationId);
        return ApiResponse.success(response, "Insurance application loaded");
    }

    @PostMapping("/{applicationId}/reviews/auto-score")
    public ApiResponse<UnderwritingAutoScoreResponse> calculateAutoScore(@PathVariable String applicationId) {
        UnderwritingAutoScoreResponse response = underwritingApplicationService.calculateAutoScore(applicationId);
        return ApiResponse.success(response, "Auto underwriting score calculated");
    }

    @PostMapping("/{applicationId}/reviews/finalize")
    public ApiResponse<UnderwritingReviewResponse> finalizeReview(
            @PathVariable String applicationId,
            @Valid @RequestBody UnderwritingFinalizeRequest request
    ) {
        UnderwritingReviewResponse response = underwritingApplicationService.finalizeReview(applicationId, request);
        return ApiResponse.success(response, "Final underwriting result saved");
    }

    @GetMapping("/{applicationId}/history")
    public ApiResponse<List<UnderwritingHistoryResponse>> getHistory(@PathVariable String applicationId) {
        List<UnderwritingHistoryResponse> response = underwritingApplicationService.getHistory(applicationId);
        return ApiResponse.success(response, "Underwriting history loaded");
    }
}
