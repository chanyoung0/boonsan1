package model.accident;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 손해조사 도메인 모델 — 사고 손해 여부 및 손해 규모 판단 정보 관리
public class DamageInvestigation {

    private String adjusterId;
    private float faultRatio;
    private LocalDateTime investigatedAt;
    private BigDecimal lossincome;
    private BigDecimal medicalExpense;
    private BigDecimal settlementAmount;

    // 손해액 산정
    public void assessDamageCost() {}

    // 외부 조사 위탁
    public void delegateInvestigation() {}

    // 사고 청구 반려
    public void rejectClaim() {}

    // 보험사기 조사 요청
    public void requestFraudInvestigation() {}
}
