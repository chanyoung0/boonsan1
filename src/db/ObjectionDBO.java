package db;

import enums.AcceptanceStatus;
import model.accident.Objection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Objection 엔티티 DB 매핑 — objection 테이블 CRUD 담당
public class ObjectionDBO extends DBA {

    public Objection findById(String objectionId) {
        String sql = "SELECT objection_id, claimant_info, objection_reason, original_payment_details, "
                + "acceptance_status, adjusted_amount, transfer_reason "
                + "FROM objection WHERE objection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, objectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapObjection(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의신청 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Objection> findAll() {
        String sql = "SELECT objection_id, claimant_info, objection_reason, original_payment_details, "
                + "acceptance_status, adjusted_amount, transfer_reason "
                + "FROM objection ORDER BY objection_id";
        List<Objection> objectionList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                objectionList.add(mapObjection(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의신청 목록 조회 실패: " + e.getMessage());
        }
        return objectionList;
    }

    public boolean save(Objection objection) {
        if (objection == null) {
            return false;
        }
        String sql = "INSERT INTO objection "
                + "(objection_id, claimant_info, objection_reason, original_payment_details, "
                + "acceptance_status, adjusted_amount, transfer_reason) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, objection.getObjectionId());
            statement.setString(2, objection.getClaimantInfo());
            statement.setString(3, objection.getObjectionReason());
            statement.setString(4, objection.getOriginalPaymentDetails());
            statement.setString(5, resolveStatusName(objection.getAcceptanceStatus()));
            statement.setBigDecimal(6, objection.getAdjustedAmount());
            statement.setString(7, objection.getTransferReason());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의신청 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Objection objection) {
        if (objection == null) {
            return false;
        }
        String sql = "UPDATE objection SET "
                + "claimant_info = ?, objection_reason = ?, original_payment_details = ?, "
                + "acceptance_status = ?, adjusted_amount = ?, transfer_reason = ? "
                + "WHERE objection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, objection.getClaimantInfo());
            statement.setString(2, objection.getObjectionReason());
            statement.setString(3, objection.getOriginalPaymentDetails());
            statement.setString(4, resolveStatusName(objection.getAcceptanceStatus()));
            statement.setBigDecimal(5, objection.getAdjustedAmount());
            statement.setString(6, objection.getTransferReason());
            statement.setString(7, objection.getObjectionId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의신청 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String objectionId) {
        String sql = "DELETE FROM objection WHERE objection_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, objectionId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 이의신청 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Objection mapObjection(ResultSet resultSet) throws SQLException {
        Objection objection = new Objection(
                resultSet.getString("objection_id"),
                resultSet.getString("claimant_info"),
                resultSet.getString("objection_reason"),
                resultSet.getString("original_payment_details"),
                resolveStatus(resultSet.getString("acceptance_status"))
        );
        objection.setAdjustedAmount(resultSet.getBigDecimal("adjusted_amount"));
        objection.setTransferReason(resultSet.getString("transfer_reason"));
        return objection;
    }

    private String resolveStatusName(AcceptanceStatus status) {
        return status == null ? AcceptanceStatus.PENDING.name() : status.name();
    }

    private AcceptanceStatus resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return AcceptanceStatus.PENDING;
        }
        try {
            return AcceptanceStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return AcceptanceStatus.PENDING;
        }
    }
}
