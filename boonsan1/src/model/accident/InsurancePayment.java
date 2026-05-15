package model.accident;

import enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InsurancePayment {

    private String confirmId;
    private BigDecimal finalMedicalExpense;
    private BigDecimal finalSettlementAmount;
    private BigDecimal investigationCost;
    private LocalDate paidAt;
    private String paymentAccount;
    private PaymentStatus paymentStatus;
    private BigDecimal retainedEstimate;

    public void closeCase() {}

    public void generatePaymentRecord() {}

    public void rejectSubrogation() {}

    public void sendNotification() {}

    public void transfer() {}
}
