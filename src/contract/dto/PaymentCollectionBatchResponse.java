package contract.dto;

import java.math.BigDecimal;
import java.util.List;

public class PaymentCollectionBatchResponse {

    private final int targetCount;
    private final int successCount;
    private final int failureCount;
    private final BigDecimal totalCollectedAmount;
    private final List<PaymentCollectionResponse> results;

    public PaymentCollectionBatchResponse(
            int targetCount,
            int successCount,
            int failureCount,
            BigDecimal totalCollectedAmount,
            List<PaymentCollectionResponse> results
    ) {
        this.targetCount = targetCount;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.totalCollectedAmount = totalCollectedAmount;
        this.results = results;
    }

    public int getTargetCount() { return targetCount; }
    public int getSuccessCount() { return successCount; }
    public int getFailureCount() { return failureCount; }
    public BigDecimal getTotalCollectedAmount() { return totalCollectedAmount; }
    public List<PaymentCollectionResponse> getResults() { return results; }
}
