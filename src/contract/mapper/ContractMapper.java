package contract.mapper;

import model.contract.Contract;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface ContractMapper {

    Contract findByPolicyNumber(@Param("policyNumber") String policyNumber);

    int updateInsuredAmount(
            @Param("policyNumber") String policyNumber,
            @Param("insuredAmount") BigDecimal insuredAmount
    );

    int updatePaymentCycle(
            @Param("policyNumber") String policyNumber,
            @Param("paymentCycle") String paymentCycle
    );

    int updateSpecialContractList(
            @Param("policyNumber") String policyNumber,
            @Param("specialContractList") String specialContractList
    );
}
