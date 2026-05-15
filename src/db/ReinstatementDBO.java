package db;

import model.contract.Reinstatement;
import java.util.ArrayList;
import java.util.List;

// Reinstatement 엔티티 DB 매핑 — reinstatement 테이블 CRUD 담당
public class ReinstatementDBO extends DBA {

    public Reinstatement findById(String reinstatementId) {
        executeSelect("SELECT * FROM reinstatement WHERE reinstatement_id = '" + reinstatementId + "'");
        return null;
    }

    public List<Reinstatement> findAll() {
        executeSelect("SELECT * FROM reinstatement");
        return new ArrayList<>();
    }

    public void save(Reinstatement reinstatement) {
        executeInsert("INSERT INTO reinstatement (...) VALUES (...)");
    }

    public void update(Reinstatement reinstatement) {
        executeUpdate("UPDATE reinstatement SET ... WHERE reinstatement_id = ...");
    }

    public void delete(String reinstatementId) {
        executeDelete("DELETE FROM reinstatement WHERE reinstatement_id = '" + reinstatementId + "'");
    }
}
