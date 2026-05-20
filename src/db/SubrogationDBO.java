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

// Subrogation entity database operator for subrogation CRUD.
public class SubrogationDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "subrogation_id, payment_id, offender_name, offender_contact, fault_ratio, "
                    + "payment_amount, payment_deadline, deposit_account, subrogation_status";

    public Subrogation findById(String subrogationId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM subrogation WHERE subrogation_id = ?";
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
        List<Subrogation> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM subrogation ORDER BY subrogation_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapSubrogation(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT subrogation_id FROM subrogation ORDER BY subrogation_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("subrogation_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 번호 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public List<Subrogation> findByPaymentId(String paymentId) {
        List<Subrogation> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM subrogation WHERE payment_id = ? ORDER BY subrogation_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapSubrogation(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 조회 실패 (지급ID): " + e.getMessage());
        }
        return list;
    }

    public boolean save(Subrogation subrogation) {
        throw new UnsupportedOperationException("paymentId, subrogationStatus 파라미터가 필요합니다.");
    }

    public boolean save(Subrogation subrogation, String paymentId, String subrogationStatus) {
        if (subrogation == null || subrogation.getSubrogationId() == null) {
            return false;
        }
        String sql = "INSERT INTO subrogation "
                + "(subrogation_id, payment_id, offender_name, offender_contact, fault_ratio, "
                + "payment_amount, payment_deadline, deposit_account, subrogation_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, subrogation.getSubrogationId());
            statement.setString(2, paymentId);
            statement.setString(3, subrogation.getOffenderName());
            statement.setString(4, subrogation.getOffenderContact());
            statement.setFloat(5, subrogation.getFaultRatio());
            statement.setBigDecimal(6, subrogation.getPaymentAmount());
            statement.setTimestamp(7, toTimestamp(subrogation.getPaymentDeadline()));
            statement.setString(8, subrogation.getDepositAccount());
            statement.setString(9, resolveSubrogationStatusName(subrogationStatus));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Subrogation subrogation) {
        throw new UnsupportedOperationException("subrogationStatus 파라미터가 필요합니다.");
    }

    public boolean update(Subrogation subrogation, String subrogationStatus) {
        if (subrogation == null || subrogation.getSubrogationId() == null) {
            return false;
        }
        String sql = "UPDATE subrogation SET "
                + "offender_name = ?, offender_contact = ?, fault_ratio = ?, "
                + "payment_amount = ?, payment_deadline = ?, deposit_account = ?, "
                + "subrogation_status = ? "
                + "WHERE subrogation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, subrogation.getOffenderName());
            statement.setString(2, subrogation.getOffenderContact());
            statement.setFloat(3, subrogation.getFaultRatio());
            statement.setBigDecimal(4, subrogation.getPaymentAmount());
            statement.setTimestamp(5, toTimestamp(subrogation.getPaymentDeadline()));
            statement.setString(6, subrogation.getDepositAccount());
            statement.setString(7, resolveSubrogationStatusName(subrogationStatus));
            statement.setString(8, subrogation.getSubrogationId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String subrogationId) {
        String sql = "DELETE FROM subrogation WHERE subrogation_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, subrogationId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 구상권 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private Subrogation mapSubrogation(ResultSet resultSet) throws SQLException {
        Subrogation subrogation = new Subrogation();
        subrogation.setSubrogationId(resultSet.getString("subrogation_id"));
        subrogation.setPaymentId(resultSet.getString("payment_id"));
        subrogation.setOffenderName(resultSet.getString("offender_name"));
        subrogation.setOffenderContact(resultSet.getString("offender_contact"));
        subrogation.setFaultRatio(resultSet.getFloat("fault_ratio"));
        subrogation.setPaymentAmount(resultSet.getBigDecimal("payment_amount"));
        subrogation.setPaymentDeadline(toLocalDateTime(resultSet.getTimestamp("payment_deadline")));
        subrogation.setDepositAccount(resultSet.getString("deposit_account"));
        subrogation.setSubrogationStatus(resolveSubrogationStatus(resultSet.getString("subrogation_status")));
        return subrogation;
    }

    private String resolveSubrogationStatusName(String value) {
        if ("PENDING".equals(value) || "IN_PROGRESS".equals(value) || "COMPLETED".equals(value)
                || "OBJECTED".equals(value) || "CLOSED".equals(value)) {
            return value;
        }
        return SubrogationStatus.PENDING.name();
    }

    private SubrogationStatus resolveSubrogationStatus(String value) {
        if (value == null || value.trim().isEmpty()) return SubrogationStatus.PENDING;
        try {
            return SubrogationStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return SubrogationStatus.PENDING;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
