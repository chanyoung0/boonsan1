package contract;

import enums.TransferType;
import person.Manager;

import java.time.LocalDateTime;

public class Transfer {

    private Manager assignee;
    private LocalDateTime transfermedAt;
    private TransferType transferType;

    public void changeAssignee() {}

    public void processTransfer() {}
}
