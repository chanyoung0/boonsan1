package db;

import model.contract.UnpaidNotice;

import java.util.List;

public interface UnpaidNoticeMapper {

    UnpaidNotice findById(String noticeId);

    List<UnpaidNotice> findAll();

    int insert(UnpaidNotice notice);
}
