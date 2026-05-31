package db;

import model.person.Manager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 담당자 MyBatis Mapper
public interface ManagerMapper {

    Manager findByEmployeeNo(@Param("employeeNo") String employeeNo);

    List<Manager> findAll();

    int insert(@Param("mgr") Manager manager);
}
