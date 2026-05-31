package db;

import model.underwriting.UnderwritingHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 심사이력 MyBatis Mapper
public interface UnderwritingHistoryMapper {

    UnderwritingHistory findById(String historyId);

    List<UnderwritingHistory> findByInsuredPersonId(String residentRegistrationNumber);

    int insert(@Param("uwh") UnderwritingHistory underwritingHistory,
               @Param("historyId") String historyId);
}
