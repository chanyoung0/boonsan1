package db;

import enums.SurchargeCondition;
import enums.UnderwritingResultType;
import model.underwriting.UnderwritingResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// UnderwritingResult 엔티티 DB 매핑 — underwriting_result 테이블 CRUD 담당
public class UnderwritingResultDBO extends DBA {

    public UnderwritingResult findById(String resultId) {
        String sql = "SELECT result_id, underwriting_id, underwriting_result, rejection_reason, "
                + "surcharge_condition, confirmed_at "
                + "FROM underwriting_result WHERE result_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resultId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResult(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 결과 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<UnderwritingResult> findAll() {
        String sql = "SELECT result_id, underwriting_id, underwriting_result, rejection_reason, "
                + "surcharge_condition, confirmed_at "
                + "FROM underwriting_result ORDER BY confirmed_at DESC NULLS LAST, result_id";
        List<UnderwritingResult> resultList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                resultList.add(mapResult(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 결과 목록 조회 실패: " + e.getMessage());
        }
        return resultList;
    }

    public boolean save(UnderwritingResult result) {
        if (result == null) {
            return false;
        }
        String sql = "INSERT INTO underwriting_result "
                + "(result_id, underwriting_id, underwriting_result, rejection_reason, "
                + "surcharge_condition, confirmed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, result.getResultId());
            statement.setString(2, result.getUnderwritingId());
            statement.setString(3, resolveResultName(result.getUnderwritingResult()));
            statement.setString(4, result.getRejectionReason());
            statement.setString(5, resolveSurchargeName(result.getSurchargeCondition()));
            statement.setTimestamp(6, toTimestamp(result.getConfirmedAt()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 결과 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(UnderwritingResult result) {
        if (result == null) {
            return false;
        }
        String sql = "UPDATE underwriting_result SET "
                + "underwriting_id = ?, underwriting_result = ?, rejection_reason = ?, "
                + "surcharge_condition = ?, confirmed_at = ? "
                + "WHERE result_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, result.getUnderwritingId());
            statement.setString(2, resolveResultName(result.getUnderwritingResult()));
            statement.setString(3, result.getRejectionReason());
            statement.setString(4, resolveSurchargeName(result.getSurchargeCondition()));
            statement.setTimestamp(5, toTimestamp(result.getConfirmedAt()));
            statement.setString(6, result.getResultId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 결과 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String resultId) {
        String sql = "DELETE FROM underwriting_result WHERE result_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resultId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 언더라이팅 결과 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private UnderwritingResult mapResult(ResultSet resultSet) throws SQLException {
        UnderwritingResult result = new UnderwritingResult(
                resolveResult(resultSet.getString("underwriting_result")),
                resultSet.getString("rejection_reason"),
                resolveSurcharge(resultSet.getString("surcharge_condition")),
                toLocalDateTime(resultSet.getTimestamp("confirmed_at"))
        );
        result.setResultId(resultSet.getString("result_id"));
        result.setUnderwritingId(resultSet.getString("underwriting_id"));
        return result;
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveResultName(UnderwritingResultType type) {
        return type == null ? null : type.name();
    }

    private UnderwritingResultType resolveResult(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return UnderwritingResultType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveSurchargeName(SurchargeCondition condition) {
        return condition == null ? null : condition.name();
    }

    private SurchargeCondition resolveSurcharge(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return SurchargeCondition.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
