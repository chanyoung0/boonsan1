package db;

import model.contract.MaturityNotice;
import java.util.ArrayList;
import java.util.List;

// MaturityNotice 엔티티 DB 매핑 — maturity_notice 테이블 CRUD 담당
public class MaturityNoticeDBO extends DBA {

    public MaturityNotice findById(String noticeId) {
        executeSelect("SELECT * FROM maturity_notice WHERE notice_id = '" + noticeId + "'");
        return null;
    }

    public List<MaturityNotice> findAll() {
        executeSelect("SELECT * FROM maturity_notice");
        return new ArrayList<>();
    }

    public void save(MaturityNotice notice) {
        executeInsert("INSERT INTO maturity_notice (...) VALUES (...)");
    }

    public void update(MaturityNotice notice) {
        executeUpdate("UPDATE maturity_notice SET ... WHERE notice_id = ...");
    }

    public void delete(String noticeId) {
        executeDelete("DELETE FROM maturity_notice WHERE notice_id = '" + noticeId + "'");
    }
}
