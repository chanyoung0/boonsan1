package product.dto;

import java.math.BigDecimal;

// 보험요율 산정 결과 DTO — 시나리오 BP-10 (기초요율, 적용요율, 예상보험료, 손익예상치)
public class PremiumEstimateResponse {

    private final BigDecimal baseRate;
    private final BigDecimal appliedRate;
    private final BigDecimal estimatedPremium;
    private final BigDecimal profitLossEstimate;

    private PremiumEstimateResponse(
            BigDecimal baseRate,
            BigDecimal appliedRate,
            BigDecimal estimatedPremium,
            BigDecimal profitLossEstimate
    ) {
        this.baseRate = baseRate;
        this.appliedRate = appliedRate;
        this.estimatedPremium = estimatedPremium;
        this.profitLossEstimate = profitLossEstimate;
    }

    // 산정 결과 응답 생성
    public static PremiumEstimateResponse of(
            BigDecimal baseRate,
            BigDecimal appliedRate,
            BigDecimal estimatedPremium,
            BigDecimal profitLossEstimate
    ) {
        return new PremiumEstimateResponse(baseRate, appliedRate, estimatedPremium, profitLossEstimate);
    }

    public BigDecimal getBaseRate() { return baseRate; }
    public BigDecimal getAppliedRate() { return appliedRate; }
    public BigDecimal getEstimatedPremium() { return estimatedPremium; }
    public BigDecimal getProfitLossEstimate() { return profitLossEstimate; }
}
