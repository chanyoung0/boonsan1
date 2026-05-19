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
public class PayoutDBO extends DBA {

    private static final String SELECT_COLS =
            "SELECT payout_id, policy_number, processor, payment_type, calculation_basis, "
            + "calculated_amount, final_payment_amount, deduction_item, status, approved_at, paid_at "
            + "FROM payout";

    public Payout findById(String payoutId) {
        String sql = SELECT_COLS + " WHERE payout_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payoutId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPayout(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Payout> findAll() {
        List<Payout> list = new ArrayList<>();
        String sql = SELECT_COLS + " ORDER BY payout_id";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapPayout(rs));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public boolean save(Payout payout) {
        String sql = "INSERT INTO payout "
                + "(payout_id, policy_number, processor, payment_type, calculation_basis, "
                + "calculated_amount, final_payment_amount, deduction_item, status, approved_at, paid_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payout.getPayoutId());
            stmt.setString(2, payout.getPolicyNumber());
            stmt.setString(3, payout.getProcessor());
            stmt.setString(4, resolvePaymentTypeName(payout));
            stmt.setString(5, resolveCalculationBasisName(payout));
            stmt.setBigDecimal(6, payout.getCalculatedAmount());
            stmt.setBigDecimal(7, payout.getFinalPaymentAmount());
            stmt.setString(8, payout.getDeductionItem());
            stmt.setString(9, payout.getStatus());
            stmt.setTimestamp(10, toTimestamp(payout.getApprovedAt()));
            stmt.setTimestamp(11, toTimestamp(payout.getPaidAt()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 등록 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Payout payout) {
        String sql = "UPDATE payout SET status=?, approved_at=?, paid_at=? WHERE payout_id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payout.getStatus());
            stmt.setTimestamp(2, toTimestamp(payout.getApprovedAt()));
            stmt.setTimestamp(3, toTimestamp(payout.getPaidAt()));
            stmt.setString(4, payout.getPayoutId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String payoutId) {
        String sql = "DELETE FROM payout WHERE payout_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payoutId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Payout mapPayout(ResultSet rs) throws SQLException {
        Payout payout = new Payout();
        payout.setPayoutId(rs.getString("payout_id"));
        payout.setPolicyNumber(rs.getString("policy_number"));
        payout.setProcessor(rs.getString("processor"));
        payout.setPaymentType(resolvePaymentType(rs.getString("payment_type")));
        payout.setCalculationBasis(resolveCalculationBasis(rs.getString("calculation_basis")));
        payout.setCalculatedAmount(rs.getBigDecimal("calculated_amount"));
        payout.setFinalPaymentAmount(rs.getBigDecimal("final_payment_amount"));
        payout.setDeductionItem(rs.getString("deduction_item"));
        payout.setStatus(rs.getString("status"));
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) {
            payout.setApprovedAt(approvedAt.toLocalDateTime());
        }
        Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) {
            payout.setPaidAt(paidAt.toLocalDateTime());
        }
        return payout;
    }

    private String resolvePaymentTypeName(Payout payout) {
        return payout.getPaymentType() == null ? PaymentType.LUMP_SUM.name() : payout.getPaymentType().name();
    }

    private String resolveCalculationBasisName(Payout payout) {
        return payout.getCalculationBasis() == null ? CalculationBasis.MATURITY_REFUND.name() : payout.getCalculationBasis().name();
    }

    private PaymentType resolvePaymentType(String value) {
        try {
            return PaymentType.valueOf(value);
        } catch (Exception e) {
            return PaymentType.LUMP_SUM;
        }
    }

    private CalculationBasis resolveCalculationBasis(String value) {
        try {
            return CalculationBasis.valueOf(value);
        } catch (Exception e) {
            return CalculationBasis.MATURITY_REFUND;
        }
    }

    private Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }
}
