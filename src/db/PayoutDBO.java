package db;

import enums.CalculationBasis;
import enums.PaymentType;
import model.contract.Payout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Payout 엔티티 DB 매핑 — payout 테이블 CRUD 담당
// 관계 객체(damageInvestigation/insurancePayment)는 1차 DB 전환에서 제외.
public class PayoutDBO extends DBA {

    public Payout findById(String payoutId) {
        String sql = "SELECT payout_id, policy_number, processor, payment_type, calculation_basis, "
                + "calculated_amount, final_payment_amount, deduction_item, approved_at, paid_at, "
                + "cancelled, rejection_reason "
                + "FROM payout WHERE payout_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payoutId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPayout(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Payout> findAll() {
        String sql = "SELECT payout_id, policy_number, processor, payment_type, calculation_basis, "
                + "calculated_amount, final_payment_amount, deduction_item, approved_at, paid_at, "
                + "cancelled, rejection_reason "
                + "FROM payout ORDER BY approved_at DESC NULLS LAST, payout_id";
        List<Payout> payoutList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                payoutList.add(mapPayout(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 목록 조회 실패: " + e.getMessage());
        }
        return payoutList;
    }

    public boolean save(Payout payout) {
        if (payout == null) {
            return false;
        }
        String sql = "INSERT INTO payout "
                + "(payout_id, policy_number, processor, payment_type, calculation_basis, "
                + "calculated_amount, final_payment_amount, deduction_item, approved_at, paid_at, "
                + "cancelled, rejection_reason) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setPayoutParams(statement, payout);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Payout payout) {
        if (payout == null) {
            return false;
        }
        String sql = "UPDATE payout SET "
                + "policy_number = ?, processor = ?, payment_type = ?, calculation_basis = ?, "
                + "calculated_amount = ?, final_payment_amount = ?, deduction_item = ?, "
                + "approved_at = ?, paid_at = ?, cancelled = ?, rejection_reason = ? "
                + "WHERE payout_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payout.getPolicyNumber());
            statement.setString(2, payout.getProcessor());
            statement.setString(3, resolveTypeName(payout.getPaymentType()));
            statement.setString(4, resolveBasisName(payout.getCalculationBasis()));
            statement.setBigDecimal(5, payout.getCalculatedAmount());
            statement.setBigDecimal(6, payout.getFinalPaymentAmount());
            statement.setString(7, payout.getDeductionItem());
            statement.setTimestamp(8, toTimestamp(payout.getApprovedAt()));
            statement.setTimestamp(9, toTimestamp(payout.getPaidAt()));
            statement.setBoolean(10, payout.isCancelled());
            statement.setString(11, payout.getRejectionReason());
            statement.setString(12, payout.getPayoutId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String payoutId) {
        String sql = "DELETE FROM payout WHERE payout_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payoutId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Payout mapPayout(ResultSet resultSet) throws SQLException {
        Payout payout = new Payout(
                resultSet.getString("processor"),
                resolveType(resultSet.getString("payment_type")),
                resolveBasis(resultSet.getString("calculation_basis")),
                resultSet.getBigDecimal("calculated_amount"),
                resultSet.getBigDecimal("final_payment_amount"),
                resultSet.getString("deduction_item"),
                toLocalDateTime(resultSet.getTimestamp("approved_at"))
        );
        payout.setPayoutId(resultSet.getString("payout_id"));
        payout.setPolicyNumber(resultSet.getString("policy_number"));
        payout.setPaidAt(toLocalDateTime(resultSet.getTimestamp("paid_at")));
        payout.setCancelled(resultSet.getBoolean("cancelled"));
        payout.setRejectionReason(resultSet.getString("rejection_reason"));
        return payout;
    }

    private void setPayoutParams(PreparedStatement statement, Payout payout) throws SQLException {
        statement.setString(1, payout.getPayoutId());
        statement.setString(2, payout.getPolicyNumber());
        statement.setString(3, payout.getProcessor());
        statement.setString(4, resolveTypeName(payout.getPaymentType()));
        statement.setString(5, resolveBasisName(payout.getCalculationBasis()));
        statement.setBigDecimal(6, payout.getCalculatedAmount());
        statement.setBigDecimal(7, payout.getFinalPaymentAmount());
        statement.setString(8, payout.getDeductionItem());
        statement.setTimestamp(9, toTimestamp(payout.getApprovedAt()));
        statement.setTimestamp(10, toTimestamp(payout.getPaidAt()));
        statement.setBoolean(11, payout.isCancelled());
        statement.setString(12, payout.getRejectionReason());
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String resolveTypeName(PaymentType type) {
        return type == null ? null : type.name();
    }

    private PaymentType resolveType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return PaymentType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String resolveBasisName(CalculationBasis basis) {
        return basis == null ? null : basis.name();
    }

    private CalculationBasis resolveBasis(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return CalculationBasis.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
