package com.boonsan.claim.mapper;

import com.boonsan.claim.dto.SubrogationEligibilityResponse;
import com.boonsan.claim.dto.SubrogationResponse;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SubrogationMapper {

    SubrogationEligibilityResponse findEligibilityByAccidentNumber(
            @Param("accidentNumber") String accidentNumber
    );

    String findAccidentStatusByAccidentNumber(@Param("accidentNumber") String accidentNumber);

    SubrogationResponse findSubrogationByAccidentNumber(@Param("accidentNumber") String accidentNumber);

    void insertSubrogation(
            @Param("subrogationId") String subrogationId,
            @Param("accidentNumber") String accidentNumber,
            @Param("documentId") String documentId,
            @Param("investigationId") String investigationId,
            @Param("targetName") String targetName,
            @Param("subrogationReason") String subrogationReason,
            @Param("subrogationAmount") BigDecimal subrogationAmount,
            @Param("employeeNo") String employeeNo,
            @Param("subrogationStatus") String subrogationStatus,
            @Param("createdAt") LocalDateTime createdAt
    );

    int completeSubrogation(
            @Param("accidentNumber") String accidentNumber,
            @Param("currentStatus") String currentStatus,
            @Param("completedStatus") String completedStatus,
            @Param("recoveredAmount") BigDecimal recoveredAmount,
            @Param("recoveredAt") LocalDateTime recoveredAt
    );
}
