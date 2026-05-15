package db;

import model.accident.Subrogation;
import java.util.ArrayList;
import java.util.List;

// Subrogation 엔티티 DB 매핑 — subrogation 테이블 CRUD 담당
public class SubrogationDBO extends DBA {

    public Subrogation findById(String subrogationId) {
        executeSelect("SELECT * FROM subrogation WHERE subrogation_id = '" + subrogationId + "'");
        return null;
    }

    public List<Subrogation> findAll() {
        executeSelect("SELECT * FROM subrogation");
        return new ArrayList<>();
    }

    public void save(Subrogation subrogation) {
        executeInsert("INSERT INTO subrogation (...) VALUES (...)");
    }

    public void update(Subrogation subrogation) {
        executeUpdate("UPDATE subrogation SET subrogation_status = ... WHERE subrogation_id = ...");
    }

    public void delete(String subrogationId) {
        executeDelete("DELETE FROM subrogation WHERE subrogation_id = '" + subrogationId + "'");
    }
}
