package underwriting.controller;

import common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import underwriting.dto.CreditInformationInquiryCreateRequest;
import underwriting.dto.CreditInformationInquiryResponse;
import underwriting.service.CreditInformationInquiryApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/underwriting")
public class CreditInformationInquiryController {

    private final CreditInformationInquiryApplicationService creditInformationInquiryApplicationService;

    public CreditInformationInquiryController(
            CreditInformationInquiryApplicationService creditInformationInquiryApplicationService
    ) {
        this.creditInformationInquiryApplicationService = creditInformationInquiryApplicationService;
    }

    @PostMapping("/applications/{applicationId}/credit-inquiries")
    public ApiResponse<CreditInformationInquiryResponse> createInquiry(
            @PathVariable String applicationId,
            @Valid @RequestBody CreditInformationInquiryCreateRequest request
    ) {
        CreditInformationInquiryResponse response =
                creditInformationInquiryApplicationService.createInquiry(applicationId, request);
        return ApiResponse.success(response, "Credit information inquiry created");
    }

    @GetMapping("/applications/{applicationId}/credit-inquiries")
    public ApiResponse<List<CreditInformationInquiryResponse>> getInquiries(@PathVariable String applicationId) {
        List<CreditInformationInquiryResponse> response =
                creditInformationInquiryApplicationService.getInquiries(applicationId);
        return ApiResponse.success(response, "Credit information inquiry history loaded");
    }

    @GetMapping("/credit-inquiries/{inquiryId}")
    public ApiResponse<CreditInformationInquiryResponse> getInquiry(@PathVariable String inquiryId) {
        CreditInformationInquiryResponse response =
                creditInformationInquiryApplicationService.getInquiry(inquiryId);
        return ApiResponse.success(response, "Credit information inquiry loaded");
    }
}
