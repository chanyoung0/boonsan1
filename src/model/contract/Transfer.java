package model.contract;

import enums.TransferType;
import model.person.Manager;

import java.time.LocalDateTime;

// 이관 도메인 모델 — 담당자 변경 또는 부서 이관 정보 관리
public class Transfer {

    private Manager assignee;
    private LocalDateTime transferredAt;
    private TransferType transferType;

    public Transfer() {}

    // 이관 기본 정보로 초기화
    public Transfer(Manager assignee, TransferType transferType, LocalDateTime transferredAt) {
        this.assignee = assignee;
        this.transferType = transferType;
        this.transferredAt = transferredAt;
    }

    // 담당자 변경
    public void changeAssignee() {}

    // 이관 처리
    public void processTransfer() {}

    public Manager getAssignee() { return assignee; }
    public LocalDateTime getTransferredAt() { return transferredAt; }
    public TransferType getTransferType() { return transferType; }

    public void setAssignee(Manager m) { this.assignee = m; }
    public void setTransferredAt(LocalDateTime t) { this.transferredAt = t; }
    public void setTransferType(TransferType t) { this.transferType = t; }

    @Override
    public String toString() {
        return "Transfer{type=" + transferType + ", assignee=" + (assignee != null ? assignee.getName() : null)
                + ", transferredAt=" + transferredAt + "}";
    }
}
