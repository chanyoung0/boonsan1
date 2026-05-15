package model.contract;

import enums.ProcessingResult;

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

    public PaymentCollection() {}

    public PaymentCollection(LocalDate dueDate, BigDecimal collectedAmount,
                             BigDecimal unpaidAmount, int unpaidInstallmentCount,
                             ProcessingResult processingResult, LocalDateTime collectedAt) {
        this.dueDate = dueDate;
        this.collectedAmount = collectedAmount;
        this.unpaidAmount = unpaidAmount;
        this.unpaidInstallmentCount = unpaidInstallmentCount;
        this.processingResult = processingResult;
        this.collectedAt = collectedAt;
    }

    // 연체료 계산
    public void calculateLateFee() {}

    // 납기 도래 여부 확인
    public void checkDueDate() {}

    // 미납 상태 확인
    public void checkUnpaidStatus() {}

    // 수금 처리
    public void processCollection() {}

    public BigDecimal      getCollectedAmount()                     { return collectedAmount; }
    public void            setCollectedAmount(BigDecimal v)         { this.collectedAmount = v; }
    public LocalDateTime   getCollectedAt()                         { return collectedAt; }
    public void            setCollectedAt(LocalDateTime v)          { this.collectedAt = v; }
    public LocalDate       getDueDate()                             { return dueDate; }
    public void            setDueDate(LocalDate v)                  { this.dueDate = v; }
    public ProcessingResult getProcessingResult()                   { return processingResult; }
    public void            setProcessingResult(ProcessingResult v)  { this.processingResult = v; }
    public BigDecimal      getUnpaidAmount()                        { return unpaidAmount; }
    public void            setUnpaidAmount(BigDecimal v)            { this.unpaidAmount = v; }
    public int             getUnpaidInstallmentCount()              { return unpaidInstallmentCount; }
    public void            setUnpaidInstallmentCount(int v)         { this.unpaidInstallmentCount = v; }

    @Override
    public String toString() {
        return "PaymentCollection{dueDate=" + dueDate + ", collectedAmount=" + collectedAmount + ", processingResult=" + processingResult + "}";
    }
}
