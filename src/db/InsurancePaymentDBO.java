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

// InsurancePayment 엔티티 DB 매핑 — insurance_payment 테이블 CRUD 담당
public class InsurancePaymentDBO extends DBA {

    public InsurancePayment findById(String paymentId) {
        String sql = "SELECT payment_id, payment_account, processor_employee_no, "
                + "final_settlement_amount, final_repair_cost, final_medical_expense, final_lost_income, "
                + "retention_estimate, payment_status, paid_at "
                + "FROM insurance_payment WHERE payment_id = ?";
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
        String sql = "SELECT payment_id, payment_account, processor_employee_no, "
                + "final_settlement_amount, final_repair_cost, final_medical_expense, final_lost_income, "
                + "retention_estimate, payment_status, paid_at "
                + "FROM insurance_payment ORDER BY paid_at DESC NULLS LAST, payment_id";
        List<InsurancePayment> paymentList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                paymentList.add(mapPayment(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 목록 조회 실패: " + e.getMessage());
        }
        return paymentList;
    }

    public boolean save(InsurancePayment payment) {
        if (payment == null) {
            return false;
        }
        String sql = "INSERT INTO insurance_payment "
                + "(payment_id, payment_account, processor_employee_no, final_settlement_amount, "
                + "final_repair_cost, final_medical_expense, final_lost_income, retention_estimate, "
                + "payment_status, paid_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setPaymentParams(statement, payment);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 저장 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(InsurancePayment payment) {
        if (payment == null) {
            return false;
        }
        String sql = "UPDATE insurance_payment SET "
                + "payment_account = ?, processor_employee_no = ?, final_settlement_amount = ?, "
                + "final_repair_cost = ?, final_medical_expense = ?, final_lost_income = ?, "
                + "retention_estimate = ?, payment_status = ?, paid_at = ? "
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
            statement.setString(8, resolveStatusName(payment.getPaymentStatus()));
            statement.setTimestamp(9, toTimestamp(payment.getPaidAt()));
            statement.setString(10, payment.getPaymentId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String paymentId) {
        String sql = "DELETE FROM insurance_payment WHERE payment_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 보험금 지급 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private InsurancePayment mapPayment(ResultSet resultSet) throws SQLException {
        Timestamp paidTs = resultSet.getTimestamp("paid_at");
        InsurancePayment payment = new InsurancePayment(
                resultSet.getString("payment_id"),
                resultSet.getString("payment_account"),
                resultSet.getString("processor_employee_no"),
                resultSet.getBigDecimal("final_settlement_amount"),
                resultSet.getBigDecimal("final_repair_cost"),
                resultSet.getBigDecimal("final_medical_expense"),
                resultSet.getBigDecimal("final_lost_income"),
                resultSet.getBigDecimal("retention_estimate"),
                resolveStatus(resultSet.getString("payment_status"))
        );
        if (paidTs != null) {
            payment.setPaidAt(paidTs.toLocalDateTime());
        }
        return payment;
    }

    private void setPaymentParams(PreparedStatement statement, InsurancePayment payment) throws SQLException {
        statement.setString(1, payment.getPaymentId());
        statement.setString(2, payment.getPaymentAccount());
        statement.setString(3, payment.getProcessorEmployeeNo());
        statement.setBigDecimal(4, payment.getFinalSettlementAmount());
        statement.setBigDecimal(5, payment.getFinalRepairCost());
        statement.setBigDecimal(6, payment.getFinalMedicalExpense());
        statement.setBigDecimal(7, payment.getFinalLostIncome());
        statement.setBigDecimal(8, payment.getRetentionEstimate());
        statement.setString(9, resolveStatusName(payment.getPaymentStatus()));
        statement.setTimestamp(10, toTimestamp(payment.getPaidAt()));
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime == null ? null : Timestamp.valueOf(dateTime);
    }

    private String resolveStatusName(PaymentStatus status) {
        return status == null ? PaymentStatus.PENDING.name() : status.name();
    }

    private PaymentStatus resolveStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PaymentStatus.PENDING;
        }
        try {
            return PaymentStatus.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return PaymentStatus.PENDING;
        }
    }
}
