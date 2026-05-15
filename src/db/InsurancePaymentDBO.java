package db;

import model.accident.InsurancePayment;
import java.util.ArrayList;
import java.util.List;

// InsurancePayment 엔티티 DB 매핑 — insurance_payment 테이블 CRUD 담당
public class InsurancePaymentDBO extends DBA {

    public InsurancePayment findById(String paymentId) {
        executeSelect("SELECT * FROM insurance_payment WHERE payment_id = '" + paymentId + "'");
        return null;
    }

    public List<InsurancePayment> findAll() {
        executeSelect("SELECT * FROM insurance_payment");
        return new ArrayList<>();
    }

    public void save(InsurancePayment payment) {
        executeInsert("INSERT INTO insurance_payment (payment_id, payment_account, processor_employee_no, final_settlement_amount, final_repair_cost, final_medical_expense, final_lost_income, retention_estimate, payment_status, paid_at) " +
            "VALUES ('" + payment.getPaymentId() + "', '" + payment.getPaymentAccount() + "', '" +
            payment.getProcessorEmployeeNo() + "', " + payment.getFinalSettlementAmount() + ", " +
            payment.getFinalRepairCost() + ", " + payment.getFinalMedicalExpense() + ", " +
            payment.getFinalLostIncome() + ", " + payment.getRetentionEstimate() + ", '" +
            payment.getPaymentStatus() + "', '" + payment.getPaidAt() + "')");
    }

    public void update(InsurancePayment payment) {
        executeUpdate("UPDATE insurance_payment SET payment_status = '" + payment.getPaymentStatus() +
            "', paid_at = '" + payment.getPaidAt() +
            "' WHERE payment_id = '" + payment.getPaymentId() + "'");
    }

    public void delete(String paymentId) {
        executeDelete("DELETE FROM insurance_payment WHERE payment_id = '" + paymentId + "'");
    }
}
