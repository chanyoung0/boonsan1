package model.contract;

import java.time.LocalDateTime;

public class UnpaidNotice {

    private LocalDateTime dueDate;
    private LocalDateTime noticedAt;
    private LocalDateTime sentAt;

    public void calculateUnpaidAmount() {}

    public void sendNotice() {}
}
