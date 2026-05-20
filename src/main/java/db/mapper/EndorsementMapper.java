package db.mapper;

import model.contract.Endorsement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 배서 MyBatis Mapper
public interface EndorsementMapper {

    Endorsement findById(String endorsementId);

    List<Endorsement> findAll();

    List<String> findAllIds();

    int insert(@Param("end") Endorsement endorsement,
               @Param("endorsementId") String endorsementId,
               @Param("policyNumber") String policyNumber,
               @Param("endorsementType") String endorsementType,
               @Param("changeReason") String changeReason,
               @Param("uwResult") String uwResult);

    int update(@Param("end") Endorsement endorsement,
               @Param("endorsementId") String endorsementId,
               @Param("policyNumber") String policyNumber,
               @Param("endorsementType") String endorsementType,
               @Param("changeReason") String changeReason,
               @Param("uwResult") String uwResult);

    int delete(String endorsementId);

    String findPolicyNumberById(String endorsementId);

    String findEndorsementTypeById(String endorsementId);
}
