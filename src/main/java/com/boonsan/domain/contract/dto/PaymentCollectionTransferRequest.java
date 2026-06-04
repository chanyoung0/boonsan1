package com.boonsan.domain.contract.dto;

import com.boonsan.domain.enums.TransferType;
import jakarta.validation.constraints.NotNull;

public class PaymentCollectionTransferRequest {

    @NotNull
    private TransferType transferType;

    public TransferType getTransferType() { return transferType; }
    public void setTransferType(TransferType transferType) { this.transferType = transferType; }
}
