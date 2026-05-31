package db;

import model.contract.UnpaidNotice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 미납안내 MyBatis Mapper
public interface UnpaidNoticeMapper {

    UnpaidNotice findById(@Param("noticeId") String noticeId);

    List<UnpaidNotice> findByCollectionId(@Param("collectionId") String collectionId);

    int insert(@Param("un") UnpaidNotice notice,
               @Param("noticeId") String noticeId,
               @Param("collectionId") String collectionId);
}
