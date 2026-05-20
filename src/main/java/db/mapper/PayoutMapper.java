package db.mapper;

import model.contract.Payout;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 제지급금 MyBatis Mapper — PayoutDBO가 위임하는 SQL 인터페이스
public interface PayoutMapper {

    Payout findById(String payoutId);

    List<Payout> findAll();

    List<String> findAllIds();

    String findPolicyNumberById(String payoutId);

    String findStatusById(String payoutId);

    String findIdByPayout(@Param("processor") String processor,
                          @Param("paymentType") String paymentType,
                          @Param("calculationBasis") String calculationBasis,
                          @Param("calculatedAmount") java.math.BigDecimal calculatedAmount,
                          @Param("deductionItem") String deductionItem,
                          @Param("finalPaymentAmount") java.math.BigDecimal finalPaymentAmount);

    int insert(@Param("po") Payout payout,
               @Param("payoutId") String payoutId,
               @Param("policyNumber") String policyNumber,
               @Param("paymentType") String paymentType,
               @Param("calculationBasis") String calculationBasis,
               @Param("payoutStatus") String payoutStatus);

    int update(@Param("po") Payout payout,
               @Param("payoutId") String payoutId,
               @Param("policyNumber") String policyNumber,
               @Param("paymentType") String paymentType,
               @Param("calculationBasis") String calculationBasis,
               @Param("payoutStatus") String payoutStatus);

    int delete(String payoutId);
}
