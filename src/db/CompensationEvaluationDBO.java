package db;

import enums.CompensationStatus;
import enums.EvaluationResult;
import model.contract.CompensationEvaluation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// CompensationEvaluation entity database operator for compensation_evaluation CRUD.
public class CompensationEvaluationDBO extends DBA {

    public CompensationEvaluation findById(String evaluationId) {
        String sql = "SELECT evaluation_id, evaluation_month, evaluation_status, evaluation_result, "
                + "submission_agency_name, damage_amount, damage_analysis_result, compensation_statistics "
                + "FROM compensation_evaluation WHERE evaluation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, evaluationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEvaluation(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보상평가 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<CompensationEvaluation> findAll() {
        List<CompensationEvaluation> evaluationList = new ArrayList<>();
        String sql = "SELECT evaluation_id, evaluation_month, evaluation_status, evaluation_result, "
                + "submission_agency_name, damage_amount, damage_analysis_result, compensation_statistics "
                + "FROM compensation_evaluation ORDER BY evaluation_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                evaluationList.add(mapEvaluation(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보상평가 목록 조회 실패: " + e.getMessage());
        }
        return evaluationList;
    }

    public boolean save(CompensationEvaluation evaluation) {
        String sql = "INSERT INTO compensation_evaluation "
                + "(evaluation_id, evaluation_month, evaluation_status, evaluation_result, "
                + "submission_agency_name, damage_amount, damage_analysis_result, compensation_statistics) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setEvaluationParams(statement, evaluation);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보상평가 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(CompensationEvaluation evaluation) {
        String sql = "UPDATE compensation_evaluation SET "
                + "evaluation_month = ?, evaluation_status = ?, evaluation_result = ?, "
                + "submission_agency_name = ?, damage_amount = ?, damage_analysis_result = ?, "
                + "compensation_statistics = ? WHERE evaluation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, evaluation.getEvaluationMonth());
            statement.setString(2, resolveCompensationStatusName(evaluation));
            statement.setString(3, resolveEvaluationResultName(evaluation));
            statement.setString(4, evaluation.getSubmissionAgencyName());
            statement.setBigDecimal(5, evaluation.getDamageAmount());
            statement.setString(6, evaluation.getDamageAnalysisResult());
            statement.setString(7, evaluation.getCompensationStatistics());
            statement.setString(8, evaluation.getEvaluationId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보상평가 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String evaluationId) {
        String sql = "DELETE FROM compensation_evaluation WHERE evaluation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, evaluationId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보상평가 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private CompensationEvaluation mapEvaluation(ResultSet resultSet) throws SQLException {
        CompensationEvaluation evaluation = new CompensationEvaluation(
                resultSet.getString("evaluation_id"),
                resultSet.getInt("evaluation_month"),
                resolveCompensationStatus(resultSet.getString("evaluation_status")),
                resolveEvaluationResult(resultSet.getString("evaluation_result")),
                resultSet.getString("submission_agency_name")
        );
        evaluation.setDamageAmount(resultSet.getBigDecimal("damage_amount"));
        evaluation.setDamageAnalysisResult(resultSet.getString("damage_analysis_result"));
        evaluation.setCompensationStatistics(resultSet.getString("compensation_statistics"));
        return evaluation;
    }

    private void setEvaluationParams(PreparedStatement statement,
                                     CompensationEvaluation evaluation) throws SQLException {
        statement.setString(1, evaluation.getEvaluationId());
        statement.setInt(2, evaluation.getEvaluationMonth());
        statement.setString(3, resolveCompensationStatusName(evaluation));
        statement.setString(4, resolveEvaluationResultName(evaluation));
        statement.setString(5, evaluation.getSubmissionAgencyName());
        statement.setBigDecimal(6, evaluation.getDamageAmount());
        statement.setString(7, evaluation.getDamageAnalysisResult());
        statement.setString(8, evaluation.getCompensationStatistics());
    }

    private String resolveCompensationStatusName(CompensationEvaluation evaluation) {
        if (evaluation == null || evaluation.getEvaluationStatus() == null) {
            return CompensationStatus.IN_PROGRESS.name();
        }
        return evaluation.getEvaluationStatus().name();
    }

    private String resolveEvaluationResultName(CompensationEvaluation evaluation) {
        if (evaluation == null || evaluation.getEvaluationResult() == null) {
            return null;
        }
        return evaluation.getEvaluationResult().name();
    }

    private CompensationStatus resolveCompensationStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return CompensationStatus.IN_PROGRESS;
        }
        try {
            return CompensationStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return CompensationStatus.IN_PROGRESS;
        }
    }

    private EvaluationResult resolveEvaluationResult(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return EvaluationResult.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
