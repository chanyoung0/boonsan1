package db;

import model.contract.Reinstatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Reinstatement entity database operator for reinstatement CRUD.
public class ReinstatementDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "reinstatement_id, policy_number, underwriting_result, reinstatement_status, "
                    + "applied_at, processed_at";

    public Reinstatement findById(String reinstatementId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM reinstatement WHERE reinstatement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reinstatementId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapReinstatement(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Reinstatement> findAll() {
        List<Reinstatement> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM reinstatement ORDER BY reinstatement_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapReinstatement(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT reinstatement_id FROM reinstatement ORDER BY reinstatement_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("reinstatement_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 ID 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public boolean save(Reinstatement reinstatement) {
        throw new UnsupportedOperationException("reinstatementId, policyNumber, uwResult, reinstatementStatus 파라미터가 필요합니다.");
    }

    public boolean save(Reinstatement reinstatement, String reinstatementId, String policyNumber,
                        String uwResult, String reinstatementStatus) {
        if (reinstatement == null || reinstatementId == null || policyNumber == null) {
            return false;
        }
        String sql = "INSERT INTO reinstatement "
                + "(reinstatement_id, policy_number, underwriting_result, reinstatement_status, "
                + "applied_at, processed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, reinstatement, reinstatementId, policyNumber, uwResult, reinstatementStatus);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Reinstatement reinstatement) {
        throw new UnsupportedOperationException("reinstatementId, policyNumber, uwResult, reinstatementStatus 파라미터가 필요합니다.");
    }

    public boolean update(Reinstatement reinstatement, String reinstatementId, String policyNumber,
                          String uwResult, String reinstatementStatus) {
        if (reinstatement == null || reinstatementId == null || policyNumber == null) {
            return false;
        }
        String sql = "UPDATE reinstatement SET "
                + "policy_number = ?, underwriting_result = ?, reinstatement_status = ?, "
                + "applied_at = ?, processed_at = ? "
                + "WHERE reinstatement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            statement.setString(2, resolveUwResultName(uwResult));
            statement.setString(3, resolveStatusName(reinstatementStatus));
            statement.setTimestamp(4, toTimestamp(reinstatement.getAppliedAt()));
            statement.setTimestamp(5, toTimestamp(reinstatement.getProcessedAt()));
            statement.setString(6, reinstatementId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String reinstatementId) {
        String sql = "DELETE FROM reinstatement WHERE reinstatement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reinstatementId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    public String findStatusById(String reinstatementId) {
        return findStringColumnById(reinstatementId, "reinstatement_status");
    }

    public String findPolicyNumberById(String reinstatementId) {
        return findStringColumnById(reinstatementId, "policy_number");
    }

    private String findStringColumnById(String reinstatementId, String columnName) {
        String sql = "SELECT " + columnName + " FROM reinstatement WHERE reinstatement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reinstatementId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 부가정보 조회 실패: " + e.getMessage());
        }
        return null;
    }

    private Reinstatement mapReinstatement(ResultSet resultSet) throws SQLException {
        Reinstatement reinstatement = new Reinstatement();
        reinstatement.setReinstatementId(resultSet.getString("reinstatement_id"));
        reinstatement.setPolicyNumber(resultSet.getString("policy_number"));
        reinstatement.setAppliedAt(toLocalDateTime(resultSet.getTimestamp("applied_at")));
        reinstatement.setProcessedAt(toLocalDateTime(resultSet.getTimestamp("processed_at")));
        return reinstatement;
    }

    private void setSaveParams(PreparedStatement statement, Reinstatement reinstatement,
                               String reinstatementId, String policyNumber,
                               String uwResult, String reinstatementStatus) throws SQLException {
        statement.setString(1, reinstatementId);
        statement.setString(2, policyNumber);
        statement.setString(3, resolveUwResultName(uwResult));
        statement.setString(4, resolveStatusName(reinstatementStatus));
        statement.setTimestamp(5, toTimestamp(reinstatement.getAppliedAt()));
        statement.setTimestamp(6, toTimestamp(reinstatement.getProcessedAt()));
    }

    private String resolveUwResultName(String uwResult) {
        if ("APPROVED".equals(uwResult) || "SURCHARGE".equals(uwResult) || "REJECTED".equals(uwResult)) {
            return uwResult;
        }
        return "APPROVED";
    }

    private String resolveStatusName(String status) {
        if ("APPROVED".equals(status) || "REJECTED".equals(status) || "PENDING".equals(status)) {
            return status;
        }
        return "PENDING";
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
