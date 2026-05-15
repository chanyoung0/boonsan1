package db;

import model.insurance.Insurance;
import java.util.ArrayList;
import java.util.List;

// Insurance 엔티티 DB 매핑 — insurance 테이블 CRUD 담당
public class InsuranceDBO extends DBA {

    public Insurance findByProductCode(String productCode) {
        executeSelect("SELECT * FROM insurance WHERE product_code = '" + productCode + "'");
        return null;
    }

    public List<Insurance> findAll() {
        executeSelect("SELECT * FROM insurance");
        return new ArrayList<>();
    }

    public void save(Insurance insurance) {
        executeInsert("INSERT INTO insurance (...) VALUES (...)");
    }

    public void update(Insurance insurance) {
        executeUpdate("UPDATE insurance SET ... WHERE product_code = ...");
    }

    public void delete(String productCode) {
        executeDelete("DELETE FROM insurance WHERE product_code = '" + productCode + "'");
    }
}
