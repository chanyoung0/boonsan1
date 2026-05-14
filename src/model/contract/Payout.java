package model.contract;

import enums.CalculationBasis;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payout {

    private LocalDateTime approvedAt;
    private CalculationBasis calculationBasis;
    private BigDecimal calculationCost;
    private String deductions;
    private LocalDateTime paidAt;
    private String paymentAccount;
    private String processor;
    private BigDecimal reservePaymentAmount;

    public void approvePayment() {}

    public void calculatePayment() {}

    public void cancelPayment() {}

    public void processPayment() {}
}
