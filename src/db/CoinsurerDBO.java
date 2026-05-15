package db;

import model.underwriting.Coinsurer;
import java.util.ArrayList;
import java.util.List;

// Coinsurer 엔티티 DB 매핑 — coinsurer 테이블 CRUD 담당
public class CoinsurerDBO extends DBA {

    public Coinsurer findByCompanyName(String companyName) {
        executeSelect("SELECT * FROM coinsurer WHERE company_name = '" + companyName + "'");
        return null;
    }

    public List<Coinsurer> findAll() {
        executeSelect("SELECT * FROM coinsurer");
        return new ArrayList<>();
    }

    public void save(Coinsurer coinsurer) {
        executeInsert("INSERT INTO coinsurer (...) VALUES (...)");
    }

    public void update(Coinsurer coinsurer) {
        executeUpdate("UPDATE coinsurer SET is_approved = ... WHERE company_name = ...");
    }

    public void delete(String companyName) {
        executeDelete("DELETE FROM coinsurer WHERE company_name = '" + companyName + "'");
    }
}
