package db;

import model.document.Document;
import java.util.ArrayList;
import java.util.List;

// Document 엔티티 DB 매핑 — document 테이블 CRUD 담당
public class DocumentDBO extends DBA {

    public Document findById(String documentId) {
        executeSelect("SELECT * FROM document WHERE document_id = '" + documentId + "'");
        return null;
    }

    public List<Document> findAll() {
        executeSelect("SELECT * FROM document");
        return new ArrayList<>();
    }

    public void save(Document document) {
        executeInsert("INSERT INTO document (...) VALUES (...)");
    }

    public void update(Document document) {
        executeUpdate("UPDATE document SET status = ... WHERE document_id = ...");
    }

    public void delete(String documentId) {
        executeDelete("DELETE FROM document WHERE document_id = '" + documentId + "'");
    }
}
