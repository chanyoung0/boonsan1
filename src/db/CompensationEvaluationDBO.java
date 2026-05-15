package db;

import model.contract.CompensationEvaluation;
import java.util.ArrayList;
import java.util.List;

// CompensationEvaluation 엔티티 DB 매핑 — compensation_evaluation 테이블 CRUD 담당
public class CompensationEvaluationDBO extends DBA {

    public CompensationEvaluation findById(String evaluationId) {
        executeSelect("SELECT * FROM compensation_evaluation WHERE evaluation_id = '" + evaluationId + "'");
        return null;
    }

    public List<CompensationEvaluation> findAll() {
        executeSelect("SELECT * FROM compensation_evaluation");
        return new ArrayList<>();
    }

    public void save(CompensationEvaluation evaluation) {
        executeInsert("INSERT INTO compensation_evaluation (...) VALUES (...)");
    }

    public void update(CompensationEvaluation evaluation) {
        executeUpdate("UPDATE compensation_evaluation SET evaluation_status = ... WHERE evaluation_id = ...");
    }

    public void delete(String evaluationId) {
        executeDelete("DELETE FROM compensation_evaluation WHERE evaluation_id = '" + evaluationId + "'");
    }
}
