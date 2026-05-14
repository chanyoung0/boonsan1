package model.underwriting;

import java.math.BigDecimal;

// 공동보험 도메인 모델 — 위험 분담을 위한 공동인수 처리 정보 관리
public class Coinsurance {

    private float deductionRatio;
    private BigDecimal retainedAmount;
    private float retainedShareRate;

    // 보험료 할당
    public void allocatePremium() {}

    // 공동인수 신청
    public void applyForCoinsurance() {}

    // 보유액 계산
    public void calculateRetainedAmount() {}

    // 공동인수사 승인 확인
    public void checkApproval() {}

    // 출재 처리
    public void processCession() {}

    // 참여 요청 전송
    public void sendParticipationRequest() {}
}
