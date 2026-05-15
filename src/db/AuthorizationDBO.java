package db;

import model.insurance.Authorization;
import java.util.ArrayList;
import java.util.List;

// Authorization 엔티티 DB 매핑 — authorization 테이블 CRUD 담당
public class AuthorizationDBO extends DBA {

    public Authorization findById(String requestId) {
        executeSelect("SELECT * FROM authorization WHERE request_id = '" + requestId + "'");
        return null;
    }

    public List<Authorization> findAll() {
        executeSelect("SELECT * FROM authorization");
        return new ArrayList<>();
    }

    public void save(Authorization authorization) {
        executeInsert("INSERT INTO authorization (...) VALUES (...)");
    }

    public void update(Authorization authorization) {
        executeUpdate("UPDATE authorization SET is_approved = ... WHERE request_id = ...");
    }

    public void delete(String requestId) {
        executeDelete("DELETE FROM authorization WHERE request_id = '" + requestId + "'");
    }
}
