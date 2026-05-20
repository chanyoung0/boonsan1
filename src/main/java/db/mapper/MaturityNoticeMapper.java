package db.mapper;

import model.contract.MaturityNotice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 만기 안내 MyBatis Mapper — MaturityNoticeDBO가 위임하는 SQL 인터페이스
public interface MaturityNoticeMapper {

    MaturityNotice findById(String noticeId);

    List<MaturityNotice> findAll();

    List<String> findAllIds();

    int insert(@Param("mn") MaturityNotice notice,
               @Param("noticeId") String noticeId,
               @Param("deliveryMethod") String deliveryMethod,
               @Param("renewalIntention") String renewalIntention);

    int update(@Param("mn") MaturityNotice notice,
               @Param("noticeId") String noticeId,
               @Param("deliveryMethod") String deliveryMethod,
               @Param("renewalIntention") String renewalIntention);

    int delete(String noticeId);

    String findRenewalIntentionById(String noticeId);
}
