package db;

import model.underwriting.UnderwritingRequest;
import java.util.ArrayList;
import java.util.List;

// UnderwritingRequest 엔티티 DB 매핑 — underwriting_request 테이블 CRUD 담당
public class UnderwritingRequestDBO extends DBA {

    public UnderwritingRequest findById(String requestId) {
        executeSelect("SELECT * FROM underwriting_request WHERE request_id = '" + requestId + "'");
        return null;
    }

    public List<UnderwritingRequest> findAll() {
        executeSelect("SELECT * FROM underwriting_request");
        return new ArrayList<>();
    }

    public void save(UnderwritingRequest request) {
        executeInsert("INSERT INTO underwriting_request (...) VALUES (...)");
    }

    public void update(UnderwritingRequest request) {
        executeUpdate("UPDATE underwriting_request SET request_status = ... WHERE request_id = ...");
    }

    public void delete(String requestId) {
        executeDelete("DELETE FROM underwriting_request WHERE request_id = '" + requestId + "'");
    }
}
