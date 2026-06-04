package com.boonsan.model.contract;

import com.boonsan.enums.CalculationBasis;
import com.boonsan.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 지급 결정 도메인 모델 — 보험금 지급 승인 및 산정 정보 관리
public class Payout {

    private LocalDateTime approvedAt;
    private BigDecimal calculatedAmount;
    private CalculationBasis calculationBasis;
    private String deductionItem;
    private BigDecimal finalPaymentAmount;
    private LocalDateTime paidAt;
    private PaymentType paymentType;
    private String processor;

    public void approvePayment() {}

    public void calculatePayment() {}

    public void cancelPayment() {}

    public void processPayment() {}
}
