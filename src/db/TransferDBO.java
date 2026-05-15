package db;

import model.contract.Transfer;
import java.util.ArrayList;
import java.util.List;

// Transfer 엔티티 DB 매핑 — transfer 테이블 CRUD 담당
public class TransferDBO extends DBA {

    public Transfer findById(String transferId) {
        executeSelect("SELECT * FROM transfer WHERE transfer_id = '" + transferId + "'");
        return null;
    }

    public List<Transfer> findAll() {
        executeSelect("SELECT * FROM transfer");
        return new ArrayList<>();
    }

    public void save(Transfer transfer) {
        executeInsert("INSERT INTO transfer (...) VALUES (...)");
    }

    public void update(Transfer transfer) {
        executeUpdate("UPDATE transfer SET ... WHERE transfer_id = ...");
    }

    public void delete(String transferId) {
        executeDelete("DELETE FROM transfer WHERE transfer_id = '" + transferId + "'");
    }
}
