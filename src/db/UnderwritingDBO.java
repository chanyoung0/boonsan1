package db;

import enums.UnderwritingStatus;
import enums.UnderwritingType;
import model.underwriting.Underwriting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Underwriting entity database operator for underwriting CRUD.
public class UnderwritingDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "underwriting_id, underwriter_emp_no, underwriter_name, underwriter_dept, "
                    + "total_score, underwriting_status, underwriting_type, underwriting_result, "
                    + "rejection_reason, underwritten_at";

    public Underwriting findById(String underwritingId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM underwriting WHERE underwriting_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, underwritingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUnderwriting(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Underwriting> findAll() {
        List<Underwriting> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM underwriting ORDER BY underwriting_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapUnderwriting(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT underwriting_id FROM underwriting ORDER BY underwriting_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("underwriting_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 ID 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public boolean save(Underwriting underwriting) {
        throw new UnsupportedOperationException("underwritingId, empNo, empName, empDept, uwResult 파라미터가 필요합니다.");
    }

    public boolean save(Underwriting underwriting, String underwritingId, String empNo,
                        String empName, String empDept, String uwResult) {
        if (underwriting == null || underwritingId == null) {
            return false;
        }
        String sql = "INSERT INTO underwriting "
                + "(underwriting_id, underwriter_emp_no, underwriter_name, underwriter_dept, "
                + "total_score, underwriting_status, underwriting_type, underwriting_result, "
                + "rejection_reason, underwritten_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, underwriting, underwritingId, empNo, empName, empDept, uwResult);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Underwriting underwriting) {
        throw new UnsupportedOperationException("underwritingId, empNo, empName, empDept, uwResult 파라미터가 필요합니다.");
    }

    public boolean update(Underwriting underwriting, String underwritingId, String empNo,
                          String empName, String empDept, String uwResult) {
        if (underwriting == null || underwritingId == null) {
            return false;
        }
        String sql = "UPDATE underwriting SET "
                + "underwriter_emp_no = ?, underwriter_name = ?, underwriter_dept = ?, "
                + "total_score = ?, underwriting_status = ?, underwriting_type = ?, "
                + "underwriting_result = ?, rejection_reason = ?, underwritten_at = ? "
                + "WHERE underwriting_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, empNo);
            statement.setString(2, empName);
            statement.setString(3, empDept);
            statement.setFloat(4, underwriting.getTotalScore());
            statement.setString(5, resolveStatusName(underwriting));
            statement.setString(6, resolveTypeName(underwriting));
            statement.setString(7, resolveResultName(uwResult));
            statement.setString(8, underwriting.getDeductionReason());
            statement.setTimestamp(9, toTimestamp(underwriting.getUnderwrittenAt()));
            statement.setString(10, underwritingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String underwritingId) {
        String sql = "DELETE FROM underwriting WHERE underwriting_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, underwritingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    public String findStatusById(String underwritingId) {
        return findStringColumnById(underwritingId, "underwriting_status");
    }

    public String findResultById(String underwritingId) {
        return findStringColumnById(underwritingId, "underwriting_result");
    }

    private String findStringColumnById(String underwritingId, String columnName) {
        String sql = "SELECT " + columnName + " FROM underwriting WHERE underwriting_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, underwritingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 심사 부가정보 조회 실패: " + e.getMessage());
        }
        return null;
    }

    private Underwriting mapUnderwriting(ResultSet resultSet) throws SQLException {
        Underwriting underwriting = new Underwriting();
        underwriting.setUnderwritingId(resultSet.getString("underwriting_id"));
        underwriting.setUnderwriter(resultSet.getString("underwriter_name"));
        underwriting.setTotalScore(resultSet.getFloat("total_score"));
        underwriting.setUnderwritingStatus(resolveStatus(resultSet.getString("underwriting_status")));
        underwriting.setUnderwritingType(resolveType(resultSet.getString("underwriting_type")));
        underwriting.setDeductionReason(resultSet.getString("rejection_reason"));
        underwriting.setUnderwrittenAt(toLocalDateTime(resultSet.getTimestamp("underwritten_at")));
        return underwriting;
    }

    private void setSaveParams(PreparedStatement statement, Underwriting underwriting,
                               String underwritingId, String empNo, String empName,
                               String empDept, String uwResult) throws SQLException {
        statement.setString(1, underwritingId);
        statement.setString(2, empNo);
        statement.setString(3, empName);
        statement.setString(4, empDept);
        statement.setFloat(5, underwriting.getTotalScore());
        statement.setString(6, resolveStatusName(underwriting));
        statement.setString(7, resolveTypeName(underwriting));
        statement.setString(8, resolveResultName(uwResult));
        statement.setString(9, underwriting.getDeductionReason());
        statement.setTimestamp(10, toTimestamp(underwriting.getUnderwrittenAt()));
    }

    private String resolveStatusName(Underwriting underwriting) {
        if (underwriting == null || underwriting.getUnderwritingStatus() == null) {
            return UnderwritingStatus.COMPLETED.name();
        }
        return underwriting.getUnderwritingStatus().name();
    }

    private String resolveTypeName(Underwriting underwriting) {
        if (underwriting == null || underwriting.getUnderwritingType() == null) {
            return UnderwritingType.AUTO.name();
        }
        return underwriting.getUnderwritingType().name();
    }

    private String resolveResultName(String uwResult) {
        if ("APPROVED".equals(uwResult) || "SURCHARGE".equals(uwResult) || "REJECTED".equals(uwResult)) {
            return uwResult;
        }
        return "APPROVED";
    }

    private UnderwritingStatus resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return UnderwritingStatus.COMPLETED;
        }
        try {
            return UnderwritingStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return UnderwritingStatus.COMPLETED;
        }
    }

    private UnderwritingType resolveType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return UnderwritingType.AUTO;
        }
        try {
            return UnderwritingType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return UnderwritingType.AUTO;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
