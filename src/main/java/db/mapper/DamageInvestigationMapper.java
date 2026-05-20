package db.mapper;

import model.accident.DamageInvestigation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 손해조사 MyBatis Mapper
public interface DamageInvestigationMapper {

    DamageInvestigation findById(String investigationId);

    List<DamageInvestigation> findAll();

    List<String> findAllIds();

    int insert(@Param("di") DamageInvestigation investigation,
               @Param("reportNo") String reportNo,
               @Param("investigationStatus") String investigationStatus);

    int update(@Param("di") DamageInvestigation investigation,
               @Param("reportNo") String reportNo,
               @Param("investigationStatus") String investigationStatus);

    int delete(String investigationId);

    String findStatusById(String investigationId);

    String findReportNoById(String investigationId);
}
