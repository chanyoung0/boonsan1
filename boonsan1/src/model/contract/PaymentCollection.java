package model.contract;

import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentCollection {

    private BigDecimal collectedAmount;
    private LocalDate collectedAt;
    private LocalDate dueDate;
    private PaymentMethod paymentMethod;
    private BigDecimal unpaidAmount;
    private int unpaidInstallmentCount;

    public void calculateFee() {}

    public void checkDueDate() {}

    public void checkUnpaidStatus() {}

    public void processCollection() {}
}
