package com.boonsan.domain.accident.mapper;

import com.boonsan.domain.accident.dto.ObjectionEligibilityResponse;
import com.boonsan.domain.accident.dto.ObjectionResponse;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface ObjectionMapper {

    ObjectionEligibilityResponse findEligibilityByAccidentNumber(
            @Param("accidentNumber") String accidentNumber
    );

    ObjectionResponse findObjectionByAccidentNumber(@Param("accidentNumber") String accidentNumber);

    void insertObjection(
            @Param("objectionId") String objectionId,
            @Param("accidentNumber") String accidentNumber,
            @Param("claimantName") String claimantName,
            @Param("claimantPhone") String claimantPhone,
            @Param("objectionReason") String objectionReason,
            @Param("requestedAction") String requestedAction,
            @Param("employeeNo") String employeeNo,
            @Param("objectionStatus") String objectionStatus,
            @Param("createdAt") LocalDateTime createdAt
    );

    int updateObjectionStatus(
            @Param("accidentNumber") String accidentNumber,
            @Param("currentStatus") String currentStatus,
            @Param("nextStatus") String nextStatus,
            @Param("updatedAt") LocalDateTime updatedAt,
            @Param("complete") boolean complete
    );
}
