package com.boonsan.domain.contract.mapper;

import com.boonsan.domain.model.contract.Contract;
import org.apache.ibatis.annotations.Param;

public interface ContractMapper {

    Contract findByPolicyNumber(@Param("policyNumber") String policyNumber);
}
