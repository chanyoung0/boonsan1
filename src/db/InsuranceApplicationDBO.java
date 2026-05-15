package db;

import model.underwriting.InsuranceApplication;
import java.util.ArrayList;
import java.util.List;

// InsuranceApplication 엔티티 DB 매핑 — insurance_application 테이블 CRUD 담당
public class InsuranceApplicationDBO extends DBA {

    public InsuranceApplication findById(String applicationId) {
        executeSelect("SELECT * FROM insurance_application WHERE application_id = '" + applicationId + "'");
        return null;
    }

    public List<InsuranceApplication> findAll() {
        executeSelect("SELECT * FROM insurance_application");
        return new ArrayList<>();
    }

    public void save(InsuranceApplication application) {
        executeInsert("INSERT INTO insurance_application (application_id, application_status, ...) VALUES (...)");
    }

    public void update(InsuranceApplication application) {
        executeUpdate("UPDATE insurance_application SET application_status = ... WHERE application_id = '" + application.getApplicationId() + "'");
    }

    public void delete(String applicationId) {
        executeDelete("DELETE FROM insurance_application WHERE application_id = '" + applicationId + "'");
    }
}
