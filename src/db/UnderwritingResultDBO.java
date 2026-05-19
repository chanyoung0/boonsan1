package db;

import model.underwriting.UnderwritingResult;
import java.util.ArrayList;
import java.util.List;

// UnderwritingResult 엔티티 DB 매핑 — underwriting_result 테이블 CRUD 담당
public class UnderwritingResultDBO extends DBA {

    public UnderwritingResult findById(String resultId) {
        executeSelect("SELECT * FROM underwriting_result WHERE result_id = '" + resultId + "'");
        return null;
    }

    public List<UnderwritingResult> findAll() {
        executeSelect("SELECT * FROM underwriting_result");
        return new ArrayList<>();
    }

    public void save(UnderwritingResult result) {
        executeInsert("INSERT INTO underwriting_result (underwriting_result, rejection_reason, surcharge_condition, confirmed_at) " +
            "VALUES ('" + result.getUnderwritingResult() + "', '" + result.getRejectionReason() + "', '" +
            result.getSurchargeCondition() + "', '" + result.getConfirmedAt() + "')");
    }

    public void update(UnderwritingResult result) {
        executeUpdate("UPDATE underwriting_result SET underwriting_result = '" + result.getUnderwritingResult() +
            "', confirmed_at = '" + result.getConfirmedAt() + "'");
    }

    public void delete(String resultId) {
        executeDelete("DELETE FROM underwriting_result WHERE result_id = '" + resultId + "'");
    }
}
