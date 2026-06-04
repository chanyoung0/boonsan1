package com.boonsan.domain.model.contract;

import com.boonsan.domain.enums.TransferType;
import com.boonsan.domain.model.person.Manager;

import java.time.LocalDateTime;

public class Transfer {

    private Manager assignee;
    private LocalDateTime transferredAt;
    private TransferType transferType;

    public void changeAssignee() {}

    public void processTransfer() {}
}
