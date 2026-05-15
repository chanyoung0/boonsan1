package model.contract;

import enums.ProcessingResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 분납/수금 도메인 모델 — 보험료 납입 및 수금 처리 정보 관리
public class PaymentCollection {

    private BigDecimal collectedAmount;
    private LocalDateTime collectedAt;
    private LocalDate dueDate;
    private ProcessingResult processingResult;
    private BigDecimal unpaidAmount;
    private int unpaidInstallmentCount;
    private final List<Transfer> transfers = new ArrayList<>();
    private final List<UnpaidNotice> unpaidNotices = new ArrayList<>();

    public PaymentCollection() {}

    // 수금 기본 정보로 초기화
    public PaymentCollection(LocalDate dueDate, BigDecimal collectedAmount, BigDecimal unpaidAmount) {
        this.dueDate = dueDate;
        this.collectedAmount = collectedAmount;
        this.unpaidAmount = unpaidAmount;
    }

    // 연체료 계산
    public void calculateLateFee() {}

    // 납기 도래 여부 확인
    public void checkDueDate() {}

    // 미납 상태 확인
    public void checkUnpaidStatus() {}

    // 수금 처리
    public void processCollection() {}

    public BigDecimal getCollectedAmount() { return collectedAmount; }
    public LocalDateTime getCollectedAt() { return collectedAt; }
    public LocalDate getDueDate() { return dueDate; }
    public ProcessingResult getProcessingResult() { return processingResult; }
    public BigDecimal getUnpaidAmount() { return unpaidAmount; }
    public int getUnpaidInstallmentCount() { return unpaidInstallmentCount; }
    public List<Transfer> getTransfers() { return transfers; }
    public List<UnpaidNotice> getUnpaidNotices() { return unpaidNotices; }

    public void setCollectedAmount(BigDecimal v) { this.collectedAmount = v; }
    public void setCollectedAt(LocalDateTime t) { this.collectedAt = t; }
    public void setDueDate(LocalDate d) { this.dueDate = d; }
    public void setProcessingResult(ProcessingResult r) { this.processingResult = r; }
    public void setUnpaidAmount(BigDecimal v) { this.unpaidAmount = v; }
    public void setUnpaidInstallmentCount(int v) { this.unpaidInstallmentCount = v; }
    public void addTransfer(Transfer t) { this.transfers.add(t); }
    public void addUnpaidNotice(UnpaidNotice n) { this.unpaidNotices.add(n); }

    @Override
    public String toString() {
        return "PaymentCollection{due=" + dueDate + ", collected=" + collectedAmount
                + ", unpaid=" + unpaidAmount + "}";
    }
}
