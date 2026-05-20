package db;

import model.accident.InsurancePayment;
import java.util.ArrayList;
import java.util.List;

// InsurancePayment 엔티티 DB 매핑 — insurance_payment 테이블 CRUD 담당
public class InsurancePaymentDBO extends DBA {

    public InsurancePayment findById(String paymentId) {
        executeSelect("SELECT * FROM insurance_payment WHERE payment_id = '" + paymentId + "'");
        return null;
    }

    public List<InsurancePayment> findAll() {
        executeSelect("SELECT * FROM insurance_payment");
        return new ArrayList<>();
    }

    public void save(InsurancePayment payment) {
        executeInsert("INSERT INTO insurance_payment (...) VALUES (...)");
    }

    public void update(InsurancePayment payment) {
        executeUpdate("UPDATE insurance_payment SET payment_status = ... WHERE payment_id = ...");
    }

    public void delete(String paymentId) {
        executeDelete("DELETE FROM insurance_payment WHERE payment_id = '" + paymentId + "'");
    }
}
