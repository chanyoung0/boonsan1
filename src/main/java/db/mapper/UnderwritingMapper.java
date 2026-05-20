package db.mapper;

import model.underwriting.Underwriting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 보험청약 심사 MyBatis Mapper
public interface UnderwritingMapper {

    Underwriting findById(String underwritingId);

    List<Underwriting> findAll();

    List<String> findAllIds();

    int insert(@Param("uw") Underwriting underwriting,
               @Param("underwritingId") String underwritingId,
               @Param("empNo") String empNo,
               @Param("empName") String empName,
               @Param("empDept") String empDept,
               @Param("uwResult") String uwResult);

    int update(@Param("uw") Underwriting underwriting,
               @Param("underwritingId") String underwritingId,
               @Param("empNo") String empNo,
               @Param("empName") String empName,
               @Param("empDept") String empDept,
               @Param("uwResult") String uwResult);

    int delete(String underwritingId);

    String findStatusById(String underwritingId);

    String findResultById(String underwritingId);
}
