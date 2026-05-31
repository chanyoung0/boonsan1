package db;

import model.underwriting.UnderwritingResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 심사결과 MyBatis Mapper
public interface UnderwritingResultMapper {

    UnderwritingResult findById(String resultId);

    List<UnderwritingResult> findByUnderwritingId(String underwritingId);

    int insert(@Param("uwr") UnderwritingResult result);
}
