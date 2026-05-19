package db;

import model.underwriting.InsuranceApplication;
import java.util.ArrayList;
import java.util.List;

// InsuranceApplication 엔티티 DB 매핑 — insurance_application 테이블 CRUD 담당
public class InsuranceApplicationDBO extends DBA {

    public InsuranceApplication findById(String applicationId) {
        executeSelect("SELECT * FROM insurance_application WHERE application_id = ?", applicationId);
        return null;
    }

    public List<InsuranceApplication> findAll() {
        executeSelect("SELECT * FROM insurance_application");
        return new ArrayList<>();
    }

    public void save(InsuranceApplication application) {
        executeInsert(
            "INSERT INTO insurance_application (application_id, application_status, applied_at, applied_condition, " +
            "insured_amount, insured_person_info, payment_cycle, premium, product_code, terms_version) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            application.getApplicationId(), application.getApplicationStatus(), application.getAppliedAt(),
            application.getAppliedCondition(), application.getInsuredAmount(), application.getInsuredPersonInfo(),
            application.getPaymentCycle(), application.getPremium(), application.getProductCode(), application.getTermsVersion()
        );
    }

    public void update(InsuranceApplication application) {
        executeUpdate(
            "UPDATE insurance_application SET application_status = ?, applied_condition = ? WHERE application_id = ?",
            application.getApplicationStatus(), application.getAppliedCondition(), application.getApplicationId()
        );
    }

    public void delete(String applicationId) {
        executeDelete("DELETE FROM insurance_application WHERE application_id = ?", applicationId);
    }
}
