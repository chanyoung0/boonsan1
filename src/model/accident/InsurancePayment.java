package model.accident;

import enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 보험금 지급 도메인 모델 — 손해조사 결과 기반 보험금 지급 처리 정보 관리
public class InsurancePayment {

    private BigDecimal finalLostIncome;
    private BigDecimal finalMedicalExpense;
    private BigDecimal finalRepairCost;
    private BigDecimal finalSettlementAmount;
    private LocalDateTime paidAt;
    private String paymentAccount;
    private String paymentId;
    private PaymentStatus paymentStatus;
    private String processorEmployeeNo;
    private BigDecimal retentionEstimate;

    // 사건 종결
    public void closeCase() {}

    // 수령 확인
    public void confirmReceipt() {}

    // 지급 기록 생성
    public void generatePaymentRecord() {}

    // 구상권 등록
    public void registerSubrogation() {}

    // 지급 알림 발송
    public void sendNotification() {}

    // 보험금 이체
    public void transfer() {}
}
