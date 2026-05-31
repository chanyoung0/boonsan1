package db;

import model.accident.AccidentHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 사고이력 MyBatis Mapper
public interface AccidentHistoryMapper {

    AccidentHistory findById(String receiptNumber);

    List<AccidentHistory> findByHistoryId(String historyId);

    int insert(@Param("ah") AccidentHistory accidentHistory,
               @Param("historyId") String historyId);
}
