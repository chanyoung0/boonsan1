package db;

import model.underwriting.Underwriting;
import java.util.ArrayList;
import java.util.List;

// Underwriting 엔티티 DB 매핑 — underwriting 테이블 CRUD 담당
public class UnderwritingDBO extends DBA {

    public Underwriting findById(String underwritingId) {
        executeSelect("SELECT * FROM underwriting WHERE underwriting_id = '" + underwritingId + "'");
        return null;
    }

    public List<Underwriting> findAll() {
        executeSelect("SELECT * FROM underwriting");
        return new ArrayList<>();
    }

    public void save(Underwriting underwriting) {
        executeInsert("INSERT INTO underwriting (underwriter, total_score, underwriting_type, underwriting_status, is_coinsurance_recommended, deduction_reason, underwriting_opinion, underwritten_at) " +
            "VALUES ('" + underwriting.getUnderwriter() + "', " + underwriting.getTotalScore() + ", '" +
            underwriting.getUnderwritingType() + "', '" + underwriting.getUnderwritingStatus() + "', " +
            underwriting.isCoinsuranceRecommended() + ", '" + underwriting.getDeductionReason() + "', '" +
            underwriting.getUnderwritingOpinion() + "', '" + underwriting.getUnderwrittenAt() + "')");
    }

    public void update(Underwriting underwriting) {
        executeUpdate("UPDATE underwriting SET underwriting_status = '" + underwriting.getUnderwritingStatus() +
            "', total_score = " + underwriting.getTotalScore() +
            " WHERE underwriter = '" + underwriting.getUnderwriter() + "'");
    }

    public void delete(String underwritingId) {
        executeDelete("DELETE FROM underwriting WHERE underwriting_id = '" + underwritingId + "'");
    }
}
