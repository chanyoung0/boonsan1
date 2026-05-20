package db;

import model.underwriting.Coinsurance;
import java.util.ArrayList;
import java.util.List;

// Coinsurance 엔티티 DB 매핑 — coinsurance 테이블 CRUD 담당
public class CoinsuranceDBO extends DBA {

    public Coinsurance findById(String coinsuranceId) {
        executeSelect("SELECT * FROM coinsurance WHERE coinsurance_id = '" + coinsuranceId + "'");
        return null;
    }

    public List<Coinsurance> findAll() {
        executeSelect("SELECT * FROM coinsurance");
        return new ArrayList<>();
    }

    public void save(Coinsurance coinsurance) {
        executeInsert("INSERT INTO coinsurance (...) VALUES (...)");
    }

    public void update(Coinsurance coinsurance) {
        executeUpdate("UPDATE coinsurance SET ... WHERE coinsurance_id = ...");
    }

    public void delete(String coinsuranceId) {
        executeDelete("DELETE FROM coinsurance WHERE coinsurance_id = '" + coinsuranceId + "'");
    }
}
