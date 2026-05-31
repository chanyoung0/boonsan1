package db;

import model.accident.Subrogation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 구상 MyBatis Mapper
public interface SubrogationMapper {

    Subrogation findById(@Param("subrogationId") String subrogationId);

    List<Subrogation> findAll();

    String findStatusById(@Param("subrogationId") String subrogationId);

    int insert(@Param("sub") Subrogation subrogation);
}
