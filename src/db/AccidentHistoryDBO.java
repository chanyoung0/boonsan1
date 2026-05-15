package db;

import model.accident.AccidentHistory;
import java.util.ArrayList;
import java.util.List;

// AccidentHistory 엔티티 DB 매핑 — accident_history 테이블 CRUD 담당
public class AccidentHistoryDBO extends DBA {

    public AccidentHistory findByReceiptNumber(String receiptNumber) {
        executeSelect("SELECT * FROM accident_history WHERE receipt_number = '" + receiptNumber + "'");
        return null;
    }

    public List<AccidentHistory> findAll() {
        executeSelect("SELECT * FROM accident_history");
        return new ArrayList<>();
    }

    public void save(AccidentHistory history) {
        executeInsert("INSERT INTO accident_history (...) VALUES (...)");
    }

    public void update(AccidentHistory history) {
        executeUpdate("UPDATE accident_history SET ... WHERE receipt_number = ...");
    }

    public void delete(String receiptNumber) {
        executeDelete("DELETE FROM accident_history WHERE receipt_number = '" + receiptNumber + "'");
    }
}
