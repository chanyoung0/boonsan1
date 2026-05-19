package model.contract;

import enums.TransferType;
import model.person.Manager;

import java.time.LocalDateTime;

// 이관 도메인 모델 — 미납 계약 이관 및 담당자 변경 정보 관리
public class Transfer {

    private Manager assignee;
    private LocalDateTime transferredAt;
    private TransferType transferType;

    // DB 전환을 위해 추가된 필드 — 행 식별자 + 어느 분납수금 건에 대한 이관인지 추적.
    private String transferId;
    private String paymentCollectionId;

    public Transfer() {}

    public Transfer(TransferType transferType, Manager assignee, LocalDateTime transferredAt) {
        this.transferType = transferType;
        this.assignee = assignee;
        this.transferredAt = transferredAt;
    }

    public void changeAssignee() {}
    public void processTransfer(){}

    public Manager      getAssignee()                   { return assignee; }
    public void         setAssignee(Manager v)          { this.assignee = v; }
    public LocalDateTime getTransferredAt()             { return transferredAt; }
    public void         setTransferredAt(LocalDateTime v){ this.transferredAt = v; }
    public TransferType getTransferType()               { return transferType; }
    public void         setTransferType(TransferType v) { this.transferType = v; }
    public String       getTransferId()                 { return transferId; }
    public void         setTransferId(String v)         { this.transferId = v; }
    public String       getPaymentCollectionId()        { return paymentCollectionId; }
    public void         setPaymentCollectionId(String v){ this.paymentCollectionId = v; }

    @Override
    public String toString() {
        return "Transfer{transferType=" + transferType + ", transferredAt=" + transferredAt + "}";
    }
}
