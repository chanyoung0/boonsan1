package db;

import enums.PaymentStatus;
import model.accident.InsurancePayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// InsurancePayment entity database operator for insurance_payment CRUD.
public class InsurancePaymentDBO extends DBA {

    private static final String SELECT_COLUMNS =
            "payment_id, investigation_id, payment_account, processor_employee_no, "
                    + "final_settlement_amount, final_repair_cost, final_medical_expense, "
                    + "final_lost_income, retention_estimate, payment_status, paid_at";

    public InsurancePayment findById(String paymentId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM insurance_payment WHERE payment_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPayment(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<InsurancePayment> findAll() {
        List<InsurancePayment> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM insurance_payment ORDER BY payment_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapPayment(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 목록 조회 실패: " + e.getMessage());
        }
        return list;
    }

    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT payment_id FROM insurance_payment ORDER BY payment_id";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ids.add(resultSet.getString("payment_id"));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 번호 목록 조회 실패: " + e.getMessage());
        }
        return ids;
    }

    public InsurancePayment findByInvestigationId(String investigationId) {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM insurance_payment WHERE investigation_id = ? ORDER BY payment_id DESC LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, investigationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPayment(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 조회 실패 (조사ID): " + e.getMessage());
        }
        return null;
    }

    public boolean save(InsurancePayment payment) {
        throw new UnsupportedOperationException("investigationId, paymentStatus 파라미터가 필요합니다.");
    }

    public boolean save(InsurancePayment payment, String investigationId, String paymentStatus) {
        if (payment == null || payment.getPaymentId() == null) {
            return false;
        }
        String sql = "INSERT INTO insurance_payment "
                + "(payment_id, investigation_id, payment_account, processor_employee_no, "
                + "final_settlement_amount, final_repair_cost, final_medical_expense, "
                + "final_lost_income, retention_estimate, payment_status, paid_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSaveParams(statement, payment, investigationId, paymentStatus);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 저장 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean update(InsurancePayment payment) {
        throw new UnsupportedOperationException("paymentStatus 파라미터가 필요합니다.");
    }

    public boolean update(InsurancePayment payment, String paymentStatus) {
        if (payment == null || payment.getPaymentId() == null) {
            return false;
        }
        String sql = "UPDATE insurance_payment SET "
                + "payment_account = ?, processor_employee_no = ?, "
                + "final_settlement_amount = ?, final_repair_cost = ?, final_medical_expense = ?, "
                + "final_lost_income = ?, retention_estimate = ?, payment_status = ?, paid_at = ? "
                + "WHERE payment_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payment.getPaymentAccount());
            statement.setString(2, payment.getProcessorEmployeeNo());
            statement.setBigDecimal(3, payment.getFinalSettlementAmount());
            statement.setBigDecimal(4, payment.getFinalRepairCost());
            statement.setBigDecimal(5, payment.getFinalMedicalExpense());
            statement.setBigDecimal(6, payment.getFinalLostIncome());
            statement.setBigDecimal(7, payment.getRetentionEstimate());
            statement.setString(8, resolvePaymentStatusName(paymentStatus));
            statement.setTimestamp(9, toTimestamp(payment.getPaidAt()));
            statement.setString(10, payment.getPaymentId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 수정 실패: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(String paymentId) {
        String sql = "DELETE FROM insurance_payment WHERE payment_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 삭제 실패: " + e.getMessage());
        }
        return false;
    }

    private InsurancePayment mapPayment(ResultSet resultSet) throws SQLException {
        InsurancePayment payment = new InsurancePayment();
        payment.setPaymentId(resultSet.getString("payment_id"));
        payment.setInvestigationId(resultSet.getString("investigation_id"));
        payment.setPaymentAccount(resultSet.getString("payment_account"));
        payment.setProcessorEmployeeNo(resultSet.getString("processor_employee_no"));
        payment.setFinalSettlementAmount(resultSet.getBigDecimal("final_settlement_amount"));
        payment.setFinalRepairCost(resultSet.getBigDecimal("final_repair_cost"));
        payment.setFinalMedicalExpense(resultSet.getBigDecimal("final_medical_expense"));
        payment.setFinalLostIncome(resultSet.getBigDecimal("final_lost_income"));
        payment.setRetentionEstimate(resultSet.getBigDecimal("retention_estimate"));
        payment.setPaymentStatus(resolvePaymentStatus(resultSet.getString("payment_status")));
        payment.setPaidAt(toLocalDateTime(resultSet.getTimestamp("paid_at")));
        return payment;
    }

    private void setSaveParams(PreparedStatement statement, InsurancePayment payment,
                               String investigationId, String paymentStatus) throws SQLException {
        statement.setString(1, payment.getPaymentId());
        statement.setString(2, investigationId);
        statement.setString(3, payment.getPaymentAccount());
        statement.setString(4, payment.getProcessorEmployeeNo());
        statement.setBigDecimal(5, payment.getFinalSettlementAmount());
        statement.setBigDecimal(6, payment.getFinalRepairCost());
        statement.setBigDecimal(7, payment.getFinalMedicalExpense());
        statement.setBigDecimal(8, payment.getFinalLostIncome());
        statement.setBigDecimal(9, payment.getRetentionEstimate());
        statement.setString(10, resolvePaymentStatusName(paymentStatus));
        statement.setTimestamp(11, toTimestamp(payment.getPaidAt()));
    }

    private String resolvePaymentStatusName(String value) {
        if ("PENDING".equals(value) || "PAID".equals(value)
                || "REJECTED".equals(value) || "DISPUTED".equals(value) || "CLOSED".equals(value)) {
            return value;
        }
        return PaymentStatus.PENDING.name();
    }

    private PaymentStatus resolvePaymentStatus(String value) {
        if (value == null || value.trim().isEmpty()) return PaymentStatus.PENDING;
        try {
            return PaymentStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return PaymentStatus.PENDING;
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
