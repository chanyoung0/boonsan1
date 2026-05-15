package db;

import model.contract.PaymentCollection;
import java.util.ArrayList;
import java.util.List;

// PaymentCollection 엔티티 DB 매핑 — payment_collection 테이블 CRUD 담당
public class PaymentCollectionDBO extends DBA {

    public PaymentCollection findById(String collectionId) {
        executeSelect("SELECT * FROM payment_collection WHERE collection_id = '" + collectionId + "'");
        return null;
    }

    public List<PaymentCollection> findAll() {
        executeSelect("SELECT * FROM payment_collection");
        return new ArrayList<>();
    }

    public void save(PaymentCollection collection) {
        executeInsert("INSERT INTO payment_collection (...) VALUES (...)");
    }

    public void update(PaymentCollection collection) {
        executeUpdate("UPDATE payment_collection SET processing_result = ... WHERE collection_id = ...");
    }

    public void delete(String collectionId) {
        executeDelete("DELETE FROM payment_collection WHERE collection_id = '" + collectionId + "'");
    }
}
