package db;

import enums.ChangeReason;
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

// Endorsement 엔티티 DB 매핑 — endorsement 테이블 CRUD 담당
public class EndorsementDBO extends DBA {

    public Endorsement findById(String endorsementId) {
        String sql = "SELECT endorsement_id, policy_number, endorsement_type, change_reason, "
                + "previous_content, new_content, applied_at, processed_at "
                + "FROM endorsement WHERE endorsement_id = ?";
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
        String sql = "SELECT endorsement_id, policy_number, endorsement_type, change_reason, "
                + "previous_content, new_content, applied_at, processed_at "
                + "FROM endorsement ORDER BY applied_at DESC NULLS LAST, endorsement_id";
        List<Endorsement> endorsementList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                endorsementList.add(mapEndorsement(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 목록 조회 실패: " + e.getMessage());
        }
        return endorsementList;
    }

    public List<Endorsement> findByPolicyNumber(String policyNumber) {
        String sql = "SELECT endorsement_id, policy_number, endorsement_type, change_reason, "
                + "previous_content, new_content, applied_at, processed_at "
                + "FROM endorsement WHERE policy_number = ? ORDER BY applied_at DESC NULLS LAST";
        List<Endorsement> endorsementList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    endorsementList.add(mapEndorsement(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 정책별 조회 실패: " + e.getMessage());
        }
        return endorsementList;
    }

    public boolean save(Endorsement endorsement) {
        if (endorsement == null) {
            return false;
        }
        String sql = "INSERT INTO endorsement "
                + "(endorsement_id, policy_number, endorsement_type, change_reason, "
                + "previous_content, new_content, applied_at, processed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, endorsement.getEndorsementId());
            statement.setString(2, endorsement.getPolicyNumber());
            statement.setString(3, resolveTypeName(endorsement.getEndorsementType()));
            statement.setString(4, resolveReasonName(endorsement.getChangeReason()));
            statement.setString(5, endorsement.getPreviousContent());
            statement.setString(6, endorsement.getNewContent());
            statement.setTimestamp(7, toTimestamp(endorsement.getAppliedAt()));
            statement.setTimestamp(8, toTimestamp(endorsement.getProcessedAt()));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Endorsement endorsement) {
        if (endorsement == null) {
            return false;
        }
        String sql = "UPDATE endorsement SET "
                + "policy_number = ?, endorsement_type = ?, change_reason = ?, "
                + "previous_content = ?, new_content = ?, applied_at = ?, processed_at = ? "
                + "WHERE endorsement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, endorsement.getPolicyNumber());
            statement.setString(2, resolveTypeName(endorsement.getEndorsementType()));
            statement.setString(3, resolveReasonName(endorsement.getChangeReason()));
            statement.setString(4, endorsement.getPreviousContent());
            statement.setString(5, endorsement.getNewContent());
            statement.setTimestamp(6, toTimestamp(endorsement.getAppliedAt()));
            statement.setTimestamp(7, toTimestamp(endorsement.getProcessedAt()));
            statement.setString(8, endorsement.getEndorsementId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String endorsementId) {
        String sql = "DELETE FROM endorsement WHERE endorsement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, endorsementId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 배서 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Endorsement mapEndorsement(ResultSet resultSet) throws SQLException {
        Endorsement endorsement = new Endorsement(
                resolveType(resultSet.getString("endorsement_type")),
                resolveReason(resultSet.getString("change_reason")),
                resultSet.getString("previous_content"),
                resultSet.getString("new_content"),
                toLocalDateTime(resultSet.getTimestamp("applied_at"))
        );
        endorsement.setEndorsementId(resultSet.getString("endorsement_id"));
        endorsement.setPolicyNumber(resultSet.getString("policy_number"));
        endorsement.setProcessedAt(toLocalDateTime(resultSet.getTimestamp("processed_at")));
        return endorsement;
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveTypeName(EndorsementType type) {
        return type == null ? null : type.name();
    }

    private EndorsementType resolveType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return EndorsementType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveReasonName(ChangeReason reason) {
        return reason == null ? null : reason.name();
    }

    private ChangeReason resolveReason(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ChangeReason.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
