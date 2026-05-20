package db;

import model.underwriting.Reinsurance;
import java.util.ArrayList;
import java.util.List;

// Reinsurance 엔티티 DB 매핑 — reinsurance 테이블 CRUD 담당
public class ReinsuranceDBO extends DBA {

    public Reinsurance findById(String contractId) {
        executeSelect("SELECT * FROM reinsurance WHERE contract_id = '" + contractId + "'");
        return null;
    }

    public List<Reinsurance> findAll() {
        executeSelect("SELECT * FROM reinsurance");
        return new ArrayList<>();
    }

    public void save(Reinsurance reinsurance) {
        executeInsert("INSERT INTO reinsurance (...) VALUES (...)");
    }

    public void update(Reinsurance reinsurance) {
        executeUpdate("UPDATE reinsurance SET ... WHERE contract_id = ...");
    }

    public void delete(String contractId) {
        executeDelete("DELETE FROM reinsurance WHERE contract_id = '" + contractId + "'");
    }
}
