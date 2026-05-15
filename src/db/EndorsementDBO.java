package db;

import model.contract.Endorsement;
import java.util.ArrayList;
import java.util.List;

// Endorsement 엔티티 DB 매핑 — endorsement 테이블 CRUD 담당
public class EndorsementDBO extends DBA {

    public Endorsement findById(String endorsementId) {
        executeSelect("SELECT * FROM endorsement WHERE endorsement_id = '" + endorsementId + "'");
        return null;
    }

    public List<Endorsement> findAll() {
        executeSelect("SELECT * FROM endorsement");
        return new ArrayList<>();
    }

    public void save(Endorsement endorsement) {
        executeInsert("INSERT INTO endorsement (endorsement_type, change_reason, previous_content, new_content, applied_at) " +
            "VALUES ('" + endorsement.getEndorsementType() + "', '" + endorsement.getChangeReason() + "', '" +
            endorsement.getPreviousContent() + "', '" + endorsement.getNewContent() + "', '" +
            endorsement.getAppliedAt() + "')");
    }

    public void update(Endorsement endorsement) {
        executeUpdate("UPDATE endorsement SET new_content = '" + endorsement.getNewContent() +
            "', processed_at = '" + endorsement.getProcessedAt() +
            "' WHERE endorsement_type = '" + endorsement.getEndorsementType() + "' AND applied_at = '" + endorsement.getAppliedAt() + "'");
    }

    public void delete(String endorsementId) {
        executeDelete("DELETE FROM endorsement WHERE endorsement_id = '" + endorsementId + "'");
    }
}
