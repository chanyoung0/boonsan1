package model.underwriting;

import enums.SurchargeCondition;
import enums.UnderwritingResultType;

import java.time.LocalDateTime;

// 언더라이팅 결과 도메인 모델 — 심사 최종 결과 정보 관리
public class UnderwritingResult {

    private LocalDateTime confirmedAt;
    private String rejectionReason;
    private SurchargeCondition surchargeCondition;
    private UnderwritingResultType underwritingResult;

    // 심사 결과 조회
    public void getResult() {}

    // 심사 결과 등록
    public void registerResult() {}
}
