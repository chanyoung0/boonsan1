package db;

import enums.ReinstatementReason;
import model.contract.Reinstatement;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Reinstatement 엔티티 DB 매핑 — reinstatement 테이블 CRUD 담당
public class ReinstatementDBO extends DBA {

    public Reinstatement findById(String reinstatementId) {
        String sql = "SELECT reinstatement_id, policy_number, reinstatement_reason, unpaid_premium, "
                + "applied_at, desired_date, last_paid_date, has_health_changed, processed_at "
                + "FROM reinstatement WHERE reinstatement_id = ?";
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
        String sql = "SELECT reinstatement_id, policy_number, reinstatement_reason, unpaid_premium, "
                + "applied_at, desired_date, last_paid_date, has_health_changed, processed_at "
                + "FROM reinstatement ORDER BY applied_at DESC NULLS LAST, reinstatement_id";
        List<Reinstatement> reinstatementList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reinstatementList.add(mapReinstatement(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 목록 조회 실패: " + e.getMessage());
        }
        return reinstatementList;
    }

    public boolean save(Reinstatement reinstatement) {
        if (reinstatement == null) {
            return false;
        }
        String sql = "INSERT INTO reinstatement "
                + "(reinstatement_id, policy_number, reinstatement_reason, unpaid_premium, "
                + "applied_at, desired_date, last_paid_date, has_health_changed, processed_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setReinstatementParams(statement, reinstatement);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Reinstatement reinstatement) {
        if (reinstatement == null) {
            return false;
        }
        String sql = "UPDATE reinstatement SET "
                + "policy_number = ?, reinstatement_reason = ?, unpaid_premium = ?, "
                + "applied_at = ?, desired_date = ?, last_paid_date = ?, has_health_changed = ?, processed_at = ? "
                + "WHERE reinstatement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reinstatement.getPolicyNumber());
            statement.setString(2, resolveReasonName(reinstatement.getReinstatementReason()));
            statement.setBigDecimal(3, reinstatement.getUnpaidPremium());
            statement.setTimestamp(4, toTimestamp(reinstatement.getAppliedAt()));
            statement.setTimestamp(5, toTimestamp(reinstatement.getDesiredDate()));
            statement.setDate(6, toDate(reinstatement.getLastPaidDate()));
            statement.setBoolean(7, reinstatement.isHasHealthChanged());
            statement.setTimestamp(8, toTimestamp(reinstatement.getProcessedAt()));
            statement.setString(9, reinstatement.getReinstatementId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String reinstatementId) {
        String sql = "DELETE FROM reinstatement WHERE reinstatement_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reinstatementId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 부활 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Reinstatement mapReinstatement(ResultSet resultSet) throws SQLException {
        Reinstatement reinstatement = new Reinstatement(
                resolveReason(resultSet.getString("reinstatement_reason")),
                resultSet.getBigDecimal("unpaid_premium"),
                toLocalDateTime(resultSet.getTimestamp("applied_at")),
                toLocalDateTime(resultSet.getTimestamp("desired_date")),
                toLocalDate(resultSet.getDate("last_paid_date")),
                resultSet.getBoolean("has_health_changed")
        );
        reinstatement.setReinstatementId(resultSet.getString("reinstatement_id"));
        reinstatement.setPolicyNumber(resultSet.getString("policy_number"));
        reinstatement.setProcessedAt(toLocalDateTime(resultSet.getTimestamp("processed_at")));
        return reinstatement;
    }

    private void setReinstatementParams(PreparedStatement statement, Reinstatement reinstatement) throws SQLException {
        statement.setString(1, reinstatement.getReinstatementId());
        statement.setString(2, reinstatement.getPolicyNumber());
        statement.setString(3, resolveReasonName(reinstatement.getReinstatementReason()));
        statement.setBigDecimal(4, reinstatement.getUnpaidPremium());
        statement.setTimestamp(5, toTimestamp(reinstatement.getAppliedAt()));
        statement.setTimestamp(6, toTimestamp(reinstatement.getDesiredDate()));
        statement.setDate(7, toDate(reinstatement.getLastPaidDate()));
        statement.setBoolean(8, reinstatement.isHasHealthChanged());
        statement.setTimestamp(9, toTimestamp(reinstatement.getProcessedAt()));
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private Date toDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private String resolveReasonName(ReinstatementReason reason) {
        return reason == null ? null : reason.name();
    }

    private ReinstatementReason resolveReason(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ReinstatementReason.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
