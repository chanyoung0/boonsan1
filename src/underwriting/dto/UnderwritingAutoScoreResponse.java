package underwriting.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UnderwritingAutoScoreResponse {

    private String applicationId;
    private String reviewId;
    private float totalScore;
    private int totalDeduction;
    private String recommendedResult;
    private boolean autoReviewAvailable;
    private boolean manualReviewRequired;
    private boolean coinsuranceRecommended;
    private List<UnderwritingDeductionItemResponse> deductionItems;
    private String reportSummary;
    private String coinsuranceMessage;
    private String reinsuranceMessage;
    private String policyIssueMessage;
    private LocalDateTime createdAt;

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public float getTotalScore() { return totalScore; }
    public void setTotalScore(float totalScore) { this.totalScore = totalScore; }
    public int getTotalDeduction() { return totalDeduction; }
    public void setTotalDeduction(int totalDeduction) { this.totalDeduction = totalDeduction; }
    public String getRecommendedResult() { return recommendedResult; }
    public void setRecommendedResult(String recommendedResult) { this.recommendedResult = recommendedResult; }
    public boolean isAutoReviewAvailable() { return autoReviewAvailable; }
    public void setAutoReviewAvailable(boolean autoReviewAvailable) { this.autoReviewAvailable = autoReviewAvailable; }
    public boolean isManualReviewRequired() { return manualReviewRequired; }
    public void setManualReviewRequired(boolean manualReviewRequired) { this.manualReviewRequired = manualReviewRequired; }
    public boolean isCoinsuranceRecommended() { return coinsuranceRecommended; }
    public void setCoinsuranceRecommended(boolean coinsuranceRecommended) { this.coinsuranceRecommended = coinsuranceRecommended; }
    public List<UnderwritingDeductionItemResponse> getDeductionItems() { return deductionItems; }
    public void setDeductionItems(List<UnderwritingDeductionItemResponse> deductionItems) { this.deductionItems = deductionItems; }
    public String getReportSummary() { return reportSummary; }
    public void setReportSummary(String reportSummary) { this.reportSummary = reportSummary; }
    public String getCoinsuranceMessage() { return coinsuranceMessage; }
    public void setCoinsuranceMessage(String coinsuranceMessage) { this.coinsuranceMessage = coinsuranceMessage; }
    public String getReinsuranceMessage() { return reinsuranceMessage; }
    public void setReinsuranceMessage(String reinsuranceMessage) { this.reinsuranceMessage = reinsuranceMessage; }
    public String getPolicyIssueMessage() { return policyIssueMessage; }
    public void setPolicyIssueMessage(String policyIssueMessage) { this.policyIssueMessage = policyIssueMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
