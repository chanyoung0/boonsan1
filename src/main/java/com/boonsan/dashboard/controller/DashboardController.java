package com.boonsan.dashboard.controller;

import com.boonsan.common.ApiResponse;
import com.boonsan.dashboard.dto.DashboardSummaryResponse;
import com.boonsan.dashboard.service.DashboardApplicationService;
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
