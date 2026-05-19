package db;

import enums.SubrogationStatus;
import model.accident.Subrogation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Subrogation 엔티티 DB 매핑 — subrogation 테이블 CRUD 담당
public class SubrogationDBO extends DBA {

    public Subrogation findById(String subrogationId) {
        String sql = "SELECT subrogation_id, offender_name, offender_contact, fault_ratio, "
                + "payment_amount, payment_deadline, deposit_account, subrogation_status "
                + "FROM subrogation WHERE subrogation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, subrogationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSubrogation(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Subrogation> findAll() {
        String sql = "SELECT subrogation_id, offender_name, offender_contact, fault_ratio, "
                + "payment_amount, payment_deadline, deposit_account, subrogation_status "
                + "FROM subrogation ORDER BY subrogation_id";
        List<Subrogation> subrogationList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                subrogationList.add(mapSubrogation(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 목록 조회 실패: " + e.getMessage());
        }
        return subrogationList;
    }

    public boolean save(Subrogation subrogation) {
        if (subrogation == null) {
            return false;
        }
        String sql = "INSERT INTO subrogation "
                + "(subrogation_id, offender_name, offender_contact, fault_ratio, payment_amount, "
                + "payment_deadline, deposit_account, subrogation_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSubrogationParams(statement, subrogation);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Subrogation subrogation) {
        if (subrogation == null) {
            return false;
        }
        String sql = "UPDATE subrogation SET "
                + "offender_name = ?, offender_contact = ?, fault_ratio = ?, payment_amount = ?, "
                + "payment_deadline = ?, deposit_account = ?, subrogation_status = ? "
                + "WHERE subrogation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, subrogation.getOffenderName());
            statement.setString(2, subrogation.getOffenderContact());
            statement.setFloat(3, subrogation.getFaultRatio());
            statement.setBigDecimal(4, subrogation.getPaymentAmount());
            statement.setTimestamp(5, toTimestamp(subrogation.getPaymentDeadline()));
            statement.setString(6, subrogation.getDepositAccount());
            statement.setString(7, resolveStatusName(subrogation.getSubrogationStatus()));
            statement.setString(8, subrogation.getSubrogationId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String subrogationId) {
        String sql = "DELETE FROM subrogation WHERE subrogation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, subrogationId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Subrogation mapSubrogation(ResultSet resultSet) throws SQLException {
        Timestamp deadlineTs = resultSet.getTimestamp("payment_deadline");
        return new Subrogation(
                resultSet.getString("subrogation_id"),
                resultSet.getString("offender_name"),
                resultSet.getString("offender_contact"),
                resultSet.getFloat("fault_ratio"),
                resultSet.getBigDecimal("payment_amount"),
                deadlineTs == null ? null : deadlineTs.toLocalDateTime(),
                resultSet.getString("deposit_account"),
                resolveStatus(resultSet.getString("subrogation_status"))
        );
    }

    private void setSubrogationParams(PreparedStatement statement, Subrogation subrogation) throws SQLException {
        statement.setString(1, subrogation.getSubrogationId());
        statement.setString(2, subrogation.getOffenderName());
        statement.setString(3, subrogation.getOffenderContact());
        statement.setFloat(4, subrogation.getFaultRatio());
        statement.setBigDecimal(5, subrogation.getPaymentAmount());
        statement.setTimestamp(6, toTimestamp(subrogation.getPaymentDeadline()));
        statement.setString(7, subrogation.getDepositAccount());
        statement.setString(8, resolveStatusName(subrogation.getSubrogationStatus()));
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private String resolveStatusName(SubrogationStatus status) {
        return status == null ? SubrogationStatus.PENDING.name() : status.name();
    }

    private SubrogationStatus resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return SubrogationStatus.PENDING;
        }
        try {
            return SubrogationStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return SubrogationStatus.PENDING;
        }
    }
}
