package contract;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UnpaidNotice {

    private LocalDateTime dueDate;
    private LocalDateTime noticedAt;
    private LocalDateTime sentAt;

    public BigDecimal calculateUnpaidAmount() {
        return null;
    }

    public void sendNotice() {}
}
