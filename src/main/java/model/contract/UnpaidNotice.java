package model.contract;

import enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UnpaidNotice {

    private LocalDateTime dueDate;
    private PaymentMethod paymentMethod;
    private LocalDateTime sentAt;
    private BigDecimal unpaidAmount;

    public void calculateUnpaidAmount() {}

    public void sendNotice() {}
}
