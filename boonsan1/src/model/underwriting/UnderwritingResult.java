package model.underwriting;

import enums.UnderwritingResultType;

import java.time.LocalDateTime;

public class UnderwritingResult {

    private LocalDateTime confirmedAt;
    private float rejectionReason;
    private UnderwritingResultType underwritingResult;

    public UnderwritingResult getResult() {
        return null;
    }

    public void rejectResult() {}
}
