package db;

import model.accident.Objection;
import java.util.ArrayList;
import java.util.List;

// Objection 엔티티 DB 매핑 — objection 테이블 CRUD 담당
public class ObjectionDBO extends DBA {

    public Objection findById(String objectionId) {
        executeSelect("SELECT * FROM objection WHERE objection_id = '" + objectionId + "'");
        return null;
    }

    public List<Objection> findAll() {
        executeSelect("SELECT * FROM objection");
        return new ArrayList<>();
    }

    public void save(Objection objection) {
        executeInsert("INSERT INTO objection (...) VALUES (...)");
    }

    public void update(Objection objection) {
        executeUpdate("UPDATE objection SET acceptance_status = ... WHERE objection_id = ...");
    }

    public void delete(String objectionId) {
        executeDelete("DELETE FROM objection WHERE objection_id = '" + objectionId + "'");
    }
}
