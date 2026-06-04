package com.boonsan.domain.model.contract;

import com.boonsan.domain.enums.ProcessingResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 분납/수금 도메인 모델 — 보험료 납입 및 수금 처리 정보 관리
public class PaymentCollection {

    private BigDecimal collectedAmount;
    private LocalDateTime collectedAt;
    private LocalDate dueDate;
    private ProcessingResult processingResult;
    private BigDecimal unpaidAmount;
    private int unpaidInstallmentCount;

    // 연체료 계산
    public void calculateLateFee() {}

    // 납기 도래 여부 확인
    public void checkDueDate() {}

    // 미납 상태 확인
    public void checkUnpaidStatus() {}

    // 수금 처리
    public void processCollection() {}
}
