package com.boonsan.contract.mapper;

import com.boonsan.model.contract.Contract;
import org.apache.ibatis.annotations.Param;

public interface ContractMapper {

    Contract findByPolicyNumber(@Param("policyNumber") String policyNumber);
}
