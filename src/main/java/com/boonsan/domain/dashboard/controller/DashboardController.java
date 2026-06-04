package com.boonsan.domain.dashboard.controller;

import com.boonsan.global.response.ApiResponse;
import com.boonsan.domain.dashboard.dto.DashboardSummaryResponse;
import com.boonsan.domain.dashboard.service.DashboardApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardApplicationService dashboardApplicationService;

    public DashboardController(DashboardApplicationService dashboardApplicationService) {
        this.dashboardApplicationService = dashboardApplicationService;
    }

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getSummary() {
        DashboardSummaryResponse response = dashboardApplicationService.getSummary();
        return ApiResponse.success(response, "Dashboard summary loaded");
    }
}
