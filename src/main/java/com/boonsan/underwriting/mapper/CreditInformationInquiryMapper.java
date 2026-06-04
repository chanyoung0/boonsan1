package com.boonsan.underwriting.mapper;

import org.apache.ibatis.annotations.Param;
import com.boonsan.underwriting.dto.CreditInformationInquiryResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CreditInformationInquiryMapper {

    void insertInquiry(
            @Param("inquiryId") String inquiryId,
            @Param("applicationId") String applicationId,
            @Param("customerName") String customerName,
            @Param("customerIdentifierMasked") String customerIdentifierMasked,
            @Param("accidentHistoryExists") boolean accidentHistoryExists,
            @Param("otherInsuranceContractExists") boolean otherInsuranceContractExists,
            @Param("previousClaimExists") boolean previousClaimExists,
            @Param("creditRiskGrade") String creditRiskGrade,
            @Param("riskFlags") String riskFlags,
            @Param("inquiryStatus") String inquiryStatus,
            @Param("externalSystemMessage") String externalSystemMessage,
            @Param("createdAt") LocalDateTime createdAt
    );

    List<CreditInformationInquiryResponse> findByApplicationId(@Param("applicationId") String applicationId);

    CreditInformationInquiryResponse findByInquiryId(@Param("inquiryId") String inquiryId);
}
