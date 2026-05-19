package db;

import model.contract.Reinstatement;
import java.util.ArrayList;
import java.util.List;

// Reinstatement 엔티티 DB 매핑 — reinstatement 테이블 CRUD 담당
public class ReinstatementDBO extends DBA {

    public Reinstatement findById(String reinstatementId) {
        executeSelect("SELECT * FROM reinstatement WHERE reinstatement_id = '" + reinstatementId + "'");
        return null;
    }

    public List<Reinstatement> findAll() {
        executeSelect("SELECT * FROM reinstatement");
        return new ArrayList<>();
    }

    public void save(Reinstatement reinstatement) {
        executeInsert("INSERT INTO reinstatement (reinstatement_reason, unpaid_premium, applied_at, desired_date, last_paid_date, has_health_changed) " +
            "VALUES ('" + reinstatement.getReinstatementReason() + "', " + reinstatement.getUnpaidPremium() +
            ", '" + reinstatement.getAppliedAt() + "', '" + reinstatement.getDesiredDate() + "', '" +
            reinstatement.getLastPaidDate() + "', " + reinstatement.isHasHealthChanged() + ")");
    }

    public void update(Reinstatement reinstatement) {
        executeUpdate("UPDATE reinstatement SET reinstatement_reason = '" + reinstatement.getReinstatementReason() +
            "', processed_at = '" + reinstatement.getProcessedAt() +
            "' WHERE applied_at = '" + reinstatement.getAppliedAt() + "'");
    }

    public void delete(String reinstatementId) {
        executeDelete("DELETE FROM reinstatement WHERE reinstatement_id = '" + reinstatementId + "'");
    }
}
