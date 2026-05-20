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

// UnderwritingResult entity database operator for underwriting_result CRUD.
public class UnderwritingResultDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "result_id, underwriting_id, underwriting_result, rejection_reason, "
                    + "surcharge_condition, confirmed_at";

    public UnderwritingResult findById(String resultId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM underwriting_result WHERE result_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resultId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResult(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 결과 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<UnderwritingResult> findAll() {
        List<UnderwritingResult> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM underwriting_result ORDER BY result_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapResult(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 결과 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT result_id FROM underwriting_result ORDER BY result_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("result_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 결과 번호 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public List<UnderwritingResult> findByUnderwritingId(String underwritingId) {
        List<UnderwritingResult> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM underwriting_result WHERE underwriting_id = ? ORDER BY result_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, underwritingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapResult(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 결과 조회 실패 (심사ID): " + e.getMessage());
        }
        return list;
    }

    public boolean save(UnderwritingResult result) {
        throw new UnsupportedOperationException("resultId, underwritingId 파라미터가 필요합니다.");
    }

    public boolean save(UnderwritingResult result, String resultId, String underwritingId) {
        if (result == null || resultId == null || underwritingId == null) {
            return false;
        }
        String sql = "INSERT INTO underwriting_result "
                + "(result_id, underwriting_id, underwriting_result, rejection_reason, "
                + "surcharge_condition, confirmed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resultId);
            statement.setString(2, underwritingId);
            statement.setString(3, resolveResultTypeName(result.getUnderwritingResult()));
            statement.setString(4, result.getRejectionReason());
            statement.setString(5, resolveSurchargeConditionName(result.getSurchargeCondition()));
            statement.setTimestamp(6, toTimestamp(result.getConfirmedAt()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 결과 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(UnderwritingResult result) {
        throw new UnsupportedOperationException("resultId 파라미터가 필요합니다.");
    }

    public boolean update(UnderwritingResult result, String resultId) {
        if (result == null || resultId == null) {
            return false;
        }
        String sql = "UPDATE underwriting_result SET "
                + "underwriting_result = ?, rejection_reason = ?, "
                + "surcharge_condition = ?, confirmed_at = ? "
                + "WHERE result_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resolveResultTypeName(result.getUnderwritingResult()));
            statement.setString(2, result.getRejectionReason());
            statement.setString(3, resolveSurchargeConditionName(result.getSurchargeCondition()));
            statement.setTimestamp(4, toTimestamp(result.getConfirmedAt()));
            statement.setString(5, resultId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 결과 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String resultId) {
        String sql = "DELETE FROM underwriting_result WHERE result_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, resultId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 결과 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private UnderwritingResult mapResult(ResultSet resultSet) throws SQLException {
        UnderwritingResult result = new UnderwritingResult();
        result.setResultId(resultSet.getString("result_id"));
        result.setUnderwritingId(resultSet.getString("underwriting_id"));
        result.setUnderwritingResult(resolveResultType(resultSet.getString("underwriting_result")));
        result.setRejectionReason(resultSet.getString("rejection_reason"));
        result.setSurchargeCondition(resolveSurchargeCondition(resultSet.getString("surcharge_condition")));
        result.setConfirmedAt(toLocalDateTime(resultSet.getTimestamp("confirmed_at")));
        return result;
    }

    private String resolveResultTypeName(UnderwritingResultType value) {
        if (value == null) return UnderwritingResultType.PENDING.name();
        return value.name();
    }

    private UnderwritingResultType resolveResultType(String value) {
        if (value == null || value.trim().isEmpty()) return UnderwritingResultType.PENDING;
        try {
            return UnderwritingResultType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return UnderwritingResultType.PENDING;
        }
    }

    private String resolveSurchargeConditionName(SurchargeCondition value) {
        if (value == null) return SurchargeCondition.NONE.name();
        return value.name();
    }

    private SurchargeCondition resolveSurchargeCondition(String value) {
        if (value == null || value.trim().isEmpty()) return SurchargeCondition.NONE;
        try {
            return SurchargeCondition.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return SurchargeCondition.NONE;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
