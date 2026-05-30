package model.contract;

import enums.TransferType;
import model.person.Manager;

import java.time.LocalDateTime;

// 이관 도메인 모델 — 미납 계약 이관 및 담당자 변경 정보 관리
public class Transfer {

    private String transferId;
    private String collectionId;
    private Manager assignee;
    private LocalDateTime transferredAt;
    private TransferType transferType;

    public Transfer() {}

    public Transfer(TransferType transferType, Manager assignee, LocalDateTime transferredAt) {
        this.transferType = transferType;
        this.assignee = assignee;
        this.transferredAt = transferredAt;
    }

    public void changeAssignee() {}
    public void processTransfer(){}

    public String       getTransferId()                 { return transferId; }
    public void         setTransferId(String v)         { this.transferId = v; }
    public String       getCollectionId()               { return collectionId; }
    public void         setCollectionId(String v)       { this.collectionId = v; }
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
