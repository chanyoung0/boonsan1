package db;

import enums.CalculationBasis;
import enums.PaymentType;
import model.contract.Payout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// Payout entity database operator for payout CRUD.
public class PayoutDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "payout_id, policy_number, processor, payment_type, calculation_basis, "
                    + "calculated_amount, deduction_item, final_payment_amount, "
                    + "approved_at, paid_at, payout_status";

    public Payout findById(String payoutId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM payout WHERE payout_id = ?";
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
        List<Payout> payoutList = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM payout ORDER BY payout_id";
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

    public List<String> findAllIds() {
        List<String> payoutIdList = new ArrayList<>();
        String sql = "SELECT payout_id FROM payout ORDER BY payout_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                payoutIdList.add(resultSet.getString("payout_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 번호 목록 조회 실패: " + e.getMessage());
        }
        return payoutIdList;
    }

    public String findPolicyNumberById(String payoutId) {
        String sql = "SELECT policy_number FROM payout WHERE payout_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payoutId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("policy_number");
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 증권번호 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public String findStatusById(String payoutId) {
        String sql = "SELECT payout_status FROM payout WHERE payout_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payoutId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resolvePayoutStatus(resultSet.getString("payout_status"));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 상태 조회 실패: " + e.getMessage());
        }
        return "NOT_FOUND";
    }

    public String findIdByPayout(Payout payout) {
        if (payout == null) {
            return null;
        }
        String sql = "SELECT payout_id FROM payout "
                + "WHERE processor IS NOT DISTINCT FROM ? "
                + "AND payment_type IS NOT DISTINCT FROM ? "
                + "AND calculation_basis IS NOT DISTINCT FROM ? "
                + "AND calculated_amount IS NOT DISTINCT FROM ? "
                + "AND deduction_item IS NOT DISTINCT FROM ? "
                + "AND final_payment_amount IS NOT DISTINCT FROM ? "
                + "ORDER BY payout_id DESC LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payout.getProcessor());
            statement.setString(2, resolvePaymentTypeName(payout));
            statement.setString(3, resolveCalculationBasisName(payout));
            statement.setBigDecimal(4, payout.getCalculatedAmount());
            statement.setString(5, payout.getDeductionItem());
            statement.setBigDecimal(6, payout.getFinalPaymentAmount());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("payout_id");
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 번호 역조회 실패: " + e.getMessage());
        }
        return null;
    }

    public boolean save(Payout payout) {
        return false;
    }

    public boolean save(Payout payout, String payoutId, String policyNumber, String payoutStatus) {
        if (payout == null || payoutId == null || policyNumber == null) {
            return false;
        }
        String sql = "INSERT INTO payout "
                + "(payout_id, policy_number, processor, payment_type, calculation_basis, "
                + "calculated_amount, deduction_item, final_payment_amount, approved_at, paid_at, payout_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setInsertParams(statement, payout, payoutId, policyNumber, payoutStatus);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Payout payout) {
        return false;
    }

    public boolean update(Payout payout, String payoutId, String policyNumber, String payoutStatus) {
        if (payout == null || payoutId == null || policyNumber == null) {
            return false;
        }
        String sql = "UPDATE payout SET "
                + "policy_number = ?, processor = ?, payment_type = ?, calculation_basis = ?, "
                + "calculated_amount = ?, deduction_item = ?, final_payment_amount = ?, "
                + "approved_at = ?, paid_at = ?, payout_status = ? "
                + "WHERE payout_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, policyNumber);
            statement.setString(2, payout.getProcessor());
            statement.setString(3, resolvePaymentTypeName(payout));
            statement.setString(4, resolveCalculationBasisName(payout));
            statement.setBigDecimal(5, payout.getCalculatedAmount());
            statement.setString(6, payout.getDeductionItem());
            statement.setBigDecimal(7, payout.getFinalPaymentAmount());
            statement.setTimestamp(8, toTimestamp(payout.getApprovedAt()));
            statement.setTimestamp(9, toTimestamp(payout.getPaidAt()));
            statement.setString(10, resolvePayoutStatus(payoutStatus));
            statement.setString(11, payoutId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String payoutId) {
        String sql = "DELETE FROM payout WHERE payout_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payoutId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 제지급금 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private Payout mapPayout(ResultSet resultSet) throws SQLException {
        Payout payout = new Payout(
                resultSet.getString("processor"),
                resolvePaymentType(resultSet.getString("payment_type")),
                resolveCalculationBasis(resultSet.getString("calculation_basis")),
                resultSet.getBigDecimal("calculated_amount"),
                resultSet.getBigDecimal("final_payment_amount"),
                resultSet.getString("deduction_item"),
                toLocalDateTime(resultSet.getTimestamp("approved_at"))
        );
        payout.setPaidAt(toLocalDateTime(resultSet.getTimestamp("paid_at")));
        return payout;
    }

    private void setInsertParams(PreparedStatement statement, Payout payout,
                                 String payoutId, String policyNumber,
                                 String payoutStatus) throws SQLException {
        statement.setString(1, payoutId);
        statement.setString(2, policyNumber);
        statement.setString(3, payout.getProcessor());
        statement.setString(4, resolvePaymentTypeName(payout));
        statement.setString(5, resolveCalculationBasisName(payout));
        statement.setBigDecimal(6, payout.getCalculatedAmount());
        statement.setString(7, payout.getDeductionItem());
        statement.setBigDecimal(8, payout.getFinalPaymentAmount());
        statement.setTimestamp(9, toTimestamp(payout.getApprovedAt()));
        statement.setTimestamp(10, toTimestamp(payout.getPaidAt()));
        statement.setString(11, resolvePayoutStatus(payoutStatus));
    }

    private String resolvePaymentTypeName(Payout payout) {
        if (payout == null || payout.getPaymentType() == null) {
            return PaymentType.LUMP_SUM.name();
        }
        return payout.getPaymentType().name();
    }

    private PaymentType resolvePaymentType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PaymentType.LUMP_SUM;
        }
        try {
            return PaymentType.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return PaymentType.LUMP_SUM;
        }
    }

    private String resolveCalculationBasisName(Payout payout) {
        if (payout == null || payout.getCalculationBasis() == null) {
            return CalculationBasis.MATURITY_REFUND.name();
        }
        return payout.getCalculationBasis().name();
    }

    private CalculationBasis resolveCalculationBasis(String value) {
        if (value == null || value.trim().isEmpty()) {
            return CalculationBasis.MATURITY_REFUND;
        }
        try {
            return CalculationBasis.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return CalculationBasis.MATURITY_REFUND;
        }
    }

    private String resolvePayoutStatus(String value) {
        if ("APPROVED".equals(value) || "PAID".equals(value) || "CANCELLED".equals(value)) {
            return value;
        }
        return "REGISTERED";
    }

    private Timestamp toTimestamp(java.time.LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
