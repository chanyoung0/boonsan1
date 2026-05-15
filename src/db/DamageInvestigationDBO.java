package db;

import model.accident.DamageInvestigation;
import java.util.ArrayList;
import java.util.List;

// DamageInvestigation 엔티티 DB 매핑 — damage_investigation 테이블 CRUD 담당
public class DamageInvestigationDBO extends DBA {

    public DamageInvestigation findById(String investigationId) {
        executeSelect("SELECT * FROM damage_investigation WHERE investigation_id = '" + investigationId + "'");
        return null;
    }

    public List<DamageInvestigation> findAll() {
        executeSelect("SELECT * FROM damage_investigation");
        return new ArrayList<>();
    }

    public void save(DamageInvestigation investigation) {
        executeInsert("INSERT INTO damage_investigation (...) VALUES (...)");
    }

    public void update(DamageInvestigation investigation) {
        executeUpdate("UPDATE damage_investigation SET ... WHERE investigation_id = ...");
    }

    public void delete(String investigationId) {
        executeDelete("DELETE FROM damage_investigation WHERE investigation_id = '" + investigationId + "'");
    }
}
