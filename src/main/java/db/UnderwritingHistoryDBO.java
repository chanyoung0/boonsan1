package db;

import model.underwriting.UnderwritingHistory;
import java.util.ArrayList;
import java.util.List;

// UnderwritingHistory 엔티티 DB 매핑 — underwriting_history 테이블 CRUD 담당
public class UnderwritingHistoryDBO extends DBA {

    public UnderwritingHistory findById(String historyId) {
        executeSelect("SELECT * FROM underwriting_history WHERE history_id = '" + historyId + "'");
        return null;
    }

    public List<UnderwritingHistory> findAll() {
        executeSelect("SELECT * FROM underwriting_history");
        return new ArrayList<>();
    }

    public void save(UnderwritingHistory history) {
        executeInsert("INSERT INTO underwriting_history (...) VALUES (...)");
    }

    public void update(UnderwritingHistory history) {
        executeUpdate("UPDATE underwriting_history SET ... WHERE history_id = ...");
    }

    public void delete(String historyId) {
        executeDelete("DELETE FROM underwriting_history WHERE history_id = '" + historyId + "'");
    }
}
