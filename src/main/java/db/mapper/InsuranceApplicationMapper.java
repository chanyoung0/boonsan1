package db.mapper;

import model.underwriting.InsuranceApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 청약 MyBatis Mapper — InsuranceApplicationDBO가 위임하는 SQL 인터페이스
public interface InsuranceApplicationMapper {

    InsuranceApplication findById(String applicationId);

    List<InsuranceApplication> findAll();

    int insert(@Param("app") InsuranceApplication application,
               @Param("policyNumber") String policyNumber,
               @Param("applicationStatus") String applicationStatus,
               @Param("appliedCondition") String appliedCondition);

    int update(@Param("app") InsuranceApplication application,
               @Param("policyNumber") String policyNumber,
               @Param("applicationStatus") String applicationStatus);

    int delete(String applicationId);

    String findStatusById(String applicationId);

    String findPolicyNumberById(String applicationId);
}
