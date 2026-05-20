package db;

import enums.AcceptanceStatus;
import model.accident.Objection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Objection entity database operator for objection CRUD.
public class ObjectionDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "objection_id, payment_id, claimant_info, objection_reason, "
                    + "original_payment_details, transfer_reason, adjusted_amount, acceptance_status";

    public Objection findById(String objectionId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM objection WHERE objection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, objectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapObjection(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의 신청 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Objection> findAll() {
        List<Objection> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM objection ORDER BY objection_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapObjection(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의 신청 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT objection_id FROM objection ORDER BY objection_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("objection_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의 신청 번호 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public List<Objection> findByPaymentId(String paymentId) {
        List<Objection> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM objection WHERE payment_id = ? ORDER BY objection_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapObjection(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의 신청 조회 실패 (지급ID): " + e.getMessage());
        }
        return list;
    }

    public boolean save(Objection objection) {
        throw new UnsupportedOperationException("paymentId, acceptanceStatus 파라미터가 필요합니다.");
    }

    public boolean save(Objection objection, String paymentId, String acceptanceStatus) {
        if (objection == null || objection.getObjectionId() == null) {
            return false;
        }
        String sql = "INSERT INTO objection "
                + "(objection_id, payment_id, claimant_info, objection_reason, "
                + "original_payment_details, transfer_reason, adjusted_amount, acceptance_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, objection.getObjectionId());
            statement.setString(2, paymentId);
            statement.setString(3, objection.getClaimantInfo());
            statement.setString(4, objection.getObjectionReason());
            statement.setString(5, objection.getOriginalPaymentDetails());
            statement.setString(6, objection.getTransferReason());
            statement.setBigDecimal(7, objection.getAdjustedAmount());
            statement.setString(8, resolveAcceptanceStatusName(acceptanceStatus));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의 신청 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Objection objection) {
        throw new UnsupportedOperationException("acceptanceStatus 파라미터가 필요합니다.");
    }

    public boolean update(Objection objection, String acceptanceStatus) {
        if (objection == null || objection.getObjectionId() == null) {
            return false;
        }
        String sql = "UPDATE objection SET "
                + "claimant_info = ?, objection_reason = ?, original_payment_details = ?, "
                + "transfer_reason = ?, adjusted_amount = ?, acceptance_status = ? "
                + "WHERE objection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, objection.getClaimantInfo());
            statement.setString(2, objection.getObjectionReason());
            statement.setString(3, objection.getOriginalPaymentDetails());
            statement.setString(4, objection.getTransferReason());
            statement.setBigDecimal(5, objection.getAdjustedAmount());
            statement.setString(6, resolveAcceptanceStatusName(acceptanceStatus));
            statement.setString(7, objection.getObjectionId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의 신청 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String objectionId) {
        String sql = "DELETE FROM objection WHERE objection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, objectionId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의 신청 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private Objection mapObjection(ResultSet resultSet) throws SQLException {
        Objection objection = new Objection();
        objection.setObjectionId(resultSet.getString("objection_id"));
        objection.setPaymentId(resultSet.getString("payment_id"));
        objection.setClaimantInfo(resultSet.getString("claimant_info"));
        objection.setObjectionReason(resultSet.getString("objection_reason"));
        objection.setOriginalPaymentDetails(resultSet.getString("original_payment_details"));
        objection.setTransferReason(resultSet.getString("transfer_reason"));
        objection.setAdjustedAmount(resultSet.getBigDecimal("adjusted_amount"));
        objection.setAcceptanceStatus(resolveAcceptanceStatus(resultSet.getString("acceptance_status")));
        return objection;
    }

    private String resolveAcceptanceStatusName(String value) {
        if ("ACCEPTED".equals(value) || "REJECTED".equals(value)
                || "PENDING".equals(value) || "TRANSFERRED_TO_LEGAL".equals(value)) {
            return value;
        }
        return AcceptanceStatus.PENDING.name();
    }

    private AcceptanceStatus resolveAcceptanceStatus(String value) {
        if (value == null || value.trim().isEmpty()) return AcceptanceStatus.PENDING;
        try {
            return AcceptanceStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return AcceptanceStatus.PENDING;
        }
    }
}
