package contract.mapper;

import model.contract.Contract;
import org.apache.ibatis.annotations.Param;

public interface ContractMapper {

    Contract findByPolicyNumber(@Param("policyNumber") String policyNumber);
}
