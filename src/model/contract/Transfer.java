package model.contract;

import enums.TransferType;
import model.person.Manager;

import java.time.LocalDateTime;

// 이관 도메인 모델 — 미납 계약 이관 및 담당자 변경 정보 관리
public class Transfer {

    private Manager assignee;
    private LocalDateTime transferredAt;
    private TransferType transferType;

    public Transfer() {}

    public Transfer(TransferType transferType, Manager assignee, LocalDateTime transferredAt) {
        this.transferType = transferType;
        this.assignee = assignee;
        this.transferredAt = transferredAt;
    }

    // 담당자 변경
    public void changeAssignee() {
        if (assignee == null)
            throw new IllegalStateException("새 담당자 정보가 없습니다.");
    }

    // 이관 처리
    public void processTransfer() {
        if (transferType == null)
            throw new IllegalStateException("이관 유형이 설정되지 않았습니다.");
        if (this.transferredAt == null) this.transferredAt = java.time.LocalDateTime.now();
    }

    public Manager      getAssignee()                   { return assignee; }
    public void         setAssignee(Manager v)          { this.assignee = v; }
    public LocalDateTime getTransferredAt()             { return transferredAt; }
    public void         setTransferredAt(LocalDateTime v){ this.transferredAt = v; }
    public TransferType getTransferType()               { return transferType; }
    public void         setTransferType(TransferType v) { this.transferType = v; }

    @Override
    public String toString() {
        return "Transfer{transferType=" + transferType + ", transferredAt=" + transferredAt + "}";
    }
}
