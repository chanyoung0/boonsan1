package db;

import model.underwriting.UnderwritingRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 심사요청 MyBatis Mapper
public interface UnderwritingRequestMapper {

    UnderwritingRequest findById(String requestId);

    List<UnderwritingRequest> findByUnderwritingId(String underwritingId);

    int insert(@Param("uwr") UnderwritingRequest request);
}
