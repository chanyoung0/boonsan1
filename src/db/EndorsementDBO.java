package db;

import enums.EndorsementType;
import model.contract.Endorsement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Endorsement entity database operator for endorsement CRUD.
public class EndorsementDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "endorsement_id, policy_number, endorsement_type, previous_content, "
                    + "new_content, change_reason, underwriting_result, applied_at, processed_at";

    public Endorsement findById(String endorsementId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM endorsement WHERE endorsement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, endorsementId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEndorsement(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Endorsement> findAll() {
        List<Endorsement> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM endorsement ORDER BY endorsement_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapEndorsement(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT endorsement_id FROM endorsement ORDER BY endorsement_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("endorsement_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 ID 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public boolean save(Endorsement endorsement) {
        throw new UnsupportedOperationException("endorsementId, policyNumber, endorsementTypeChoice, changeReason, uwResult 파라미터가 필요합니다.");
    }

    public boolean save(Endorsement endorsement, String endorsementId, String policyNumber,
                        String endorsementTypeChoice, String changeReason, String uwResult) {
        if (endorsement == null || endorsementId == null || policyNumber == null) {
            return false;
        }
        String sql = "INSERT INTO endorsement "
                + "(endorsement_id, policy_number, endorsement_type, previous_content, "
                + "new_content, change_reason, underwriting_result, applied_at, processed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, endorsement, endorsementId, policyNumber,
                    endorsementTypeChoice, changeReason, uwResult);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Endorsement endorsement) {
        throw new UnsupportedOperationException("endorsementId, policyNumber, endorsementTypeChoice, changeReason, uwResult 파라미터가 필요합니다.");
    }

    public boolean update(Endorsement endorsement, String endorsementId, String policyNumber,
                          String endorsementTypeChoice, String changeReason, String uwResult) {
        if (endorsement == null || endorsementId == null || policyNumber == null) {
            return false;
        }
        String sql = "UPDATE endorsement SET "
                + "policy_number = ?, endorsement_type = ?, previous_content = ?, "
                + "new_content = ?, change_reason = ?, underwriting_result = ?, "
                + "applied_at = ?, processed_at = ? "
                + "WHERE endorsement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            statement.setString(2, resolveEndorsementTypeChoice(endorsementTypeChoice));
            statement.setString(3, endorsement.getPreviousContent());
            statement.setString(4, endorsement.getNewContent());
            statement.setString(5, changeReason);
            statement.setString(6, resolveUwResultName(uwResult));
            statement.setTimestamp(7, toTimestamp(endorsement.getAppliedAt()));
            statement.setTimestamp(8, toTimestamp(endorsement.getProcessedAt()));
            statement.setString(9, endorsementId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String endorsementId) {
        String sql = "DELETE FROM endorsement WHERE endorsement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, endorsementId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    public String findPolicyNumberById(String endorsementId) {
        return findStringColumnById(endorsementId, "policy_number");
    }

    public String findEndorsementTypeById(String endorsementId) {
        return findStringColumnById(endorsementId, "endorsement_type");
    }

    private String findStringColumnById(String endorsementId, String columnName) {
        String sql = "SELECT " + columnName + " FROM endorsement WHERE endorsement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, endorsementId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 부가정보 조회 실패: " + e.getMessage());
        }
        return null;
    }

    private Endorsement mapEndorsement(ResultSet resultSet) throws SQLException {
        Endorsement endorsement = new Endorsement();
        endorsement.setEndorsementId(resultSet.getString("endorsement_id"));
        endorsement.setPolicyNumber(resultSet.getString("policy_number"));
        endorsement.setEndorsementType(resolveEndorsementType(resultSet.getString("endorsement_type")));
        endorsement.setPreviousContent(resultSet.getString("previous_content"));
        endorsement.setNewContent(resultSet.getString("new_content"));
        endorsement.setAppliedAt(toLocalDateTime(resultSet.getTimestamp("applied_at")));
        endorsement.setProcessedAt(toLocalDateTime(resultSet.getTimestamp("processed_at")));
        return endorsement;
    }

    private void setSaveParams(PreparedStatement statement, Endorsement endorsement,
                               String endorsementId, String policyNumber,
                               String endorsementTypeChoice, String changeReason,
                               String uwResult) throws SQLException {
        statement.setString(1, endorsementId);
        statement.setString(2, policyNumber);
        statement.setString(3, resolveEndorsementTypeChoice(endorsementTypeChoice));
        statement.setString(4, endorsement.getPreviousContent());
        statement.setString(5, endorsement.getNewContent());
        statement.setString(6, changeReason);
        statement.setString(7, resolveUwResultName(uwResult));
        statement.setTimestamp(8, toTimestamp(endorsement.getAppliedAt()));
        statement.setTimestamp(9, toTimestamp(endorsement.getProcessedAt()));
    }

    private String resolveEndorsementTypeChoice(String choice) {
        switch (choice == null ? "" : choice) {
            case "1": return EndorsementType.COVERAGE_CHANGE.name();
            case "2": return EndorsementType.PREMIUM_CHANGE.name();
            case "3": return EndorsementType.SPECIAL_CONTRACT_CHANGE.name();
            case "4": return EndorsementType.SPECIAL_CONTRACT_CHANGE.name();
            default:  return choice;
        }
    }

    private String resolveUwResultName(String uwResult) {
        if (uwResult == null) return null;
        if (uwResult.startsWith("할증")) return "SURCHARGE";
        if (uwResult.startsWith("거절")) return "REJECTED";
        if (uwResult.startsWith("승인")) return "APPROVED";
        if ("SURCHARGE".equals(uwResult) || "REJECTED".equals(uwResult) || "APPROVED".equals(uwResult)) {
            return uwResult;
        }
        return null;
    }

    private EndorsementType resolveEndorsementType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return EndorsementType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
