package db.mapper;

import model.contract.Reinstatement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 부활 MyBatis Mapper — ReinstatementDBO가 위임하는 SQL 인터페이스
public interface ReinstatementMapper {

    Reinstatement findById(String reinstatementId);

    List<Reinstatement> findAll();

    List<String> findAllIds();

    int insert(@Param("rs") Reinstatement reinstatement,
               @Param("reinstatementId") String reinstatementId,
               @Param("policyNumber") String policyNumber,
               @Param("uwResult") String uwResult,
               @Param("reinstatementStatus") String reinstatementStatus);

    int update(@Param("rs") Reinstatement reinstatement,
               @Param("reinstatementId") String reinstatementId,
               @Param("policyNumber") String policyNumber,
               @Param("uwResult") String uwResult,
               @Param("reinstatementStatus") String reinstatementStatus);

    int delete(String reinstatementId);

    String findStatusById(String reinstatementId);

    String findPolicyNumberById(String reinstatementId);
}
