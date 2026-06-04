package com.boonsan.model.contract;

import com.boonsan.enums.TransferType;
import com.boonsan.model.person.Manager;

import java.time.LocalDateTime;

public class Transfer {

    private Manager assignee;
    private LocalDateTime transferredAt;
    private TransferType transferType;

    public void changeAssignee() {}

    public void processTransfer() {}
}
