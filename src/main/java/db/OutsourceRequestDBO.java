package db;

import model.accident.OutsourceRequest;
import java.util.ArrayList;
import java.util.List;

// OutsourceRequest 엔티티 DB 매핑 — outsource_request 테이블 CRUD 담당
public class OutsourceRequestDBO extends DBA {

    public OutsourceRequest findById(String requestId) {
        executeSelect("SELECT * FROM outsource_request WHERE request_id = '" + requestId + "'");
        return null;
    }

    public List<OutsourceRequest> findAll() {
        executeSelect("SELECT * FROM outsource_request");
        return new ArrayList<>();
    }

    public void save(OutsourceRequest request) {
        executeInsert("INSERT INTO outsource_request (...) VALUES (...)");
    }

    public void update(OutsourceRequest request) {
        executeUpdate("UPDATE outsource_request SET request_status = ... WHERE request_id = ...");
    }

    public void delete(String requestId) {
        executeDelete("DELETE FROM outsource_request WHERE request_id = '" + requestId + "'");
    }
}
