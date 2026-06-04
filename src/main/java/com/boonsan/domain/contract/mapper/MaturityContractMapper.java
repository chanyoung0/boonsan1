package com.boonsan.domain.contract.mapper;

import org.apache.ibatis.annotations.Param;

public interface MaturityContractMapper {

    int updateStatusToExpired(
            @Param("policyNumber") String policyNumber,
            @Param("currentStatus") String currentStatus,
            @Param("nextStatus") String nextStatus
    );
}
