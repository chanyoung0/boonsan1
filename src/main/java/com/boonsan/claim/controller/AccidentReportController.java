package com.boonsan.claim.controller;

import com.boonsan.claim.dto.AccidentReportCreateRequest;
import com.boonsan.claim.dto.AccidentReportResponse;
import com.boonsan.claim.service.AccidentReportApplicationService;
import com.boonsan.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims/accident-reports")
public class AccidentReportController {

    private final AccidentReportApplicationService accidentReportApplicationService;

    public AccidentReportController(AccidentReportApplicationService accidentReportApplicationService) {
        this.accidentReportApplicationService = accidentReportApplicationService;
    }

    @PostMapping
    public ApiResponse<AccidentReportResponse> create(@Valid @RequestBody AccidentReportCreateRequest request) {
        AccidentReportResponse response = accidentReportApplicationService.create(request);
        return ApiResponse.success(response, "Accident report registered");
    }

    @GetMapping("/{accidentNumber}")
    public ApiResponse<AccidentReportResponse> findByAccidentNumber(@PathVariable String accidentNumber) {
        AccidentReportResponse response = accidentReportApplicationService.findByAccidentNumber(accidentNumber);
        return ApiResponse.success(response, "Accident report found");
    }
}
