package com.boonsan.domain.model.underwriting;

import com.boonsan.domain.enums.UnderwritingStatus;
import com.boonsan.domain.enums.UnderwritingTerm;
import com.boonsan.domain.enums.UnderwritingType;

import java.time.LocalDateTime;

// 언더라이팅 도메인 모델 — 청약 위험도 평가 및 인수 심사 정보 관리
public class Underwriting {

    private String deductionReason;
    private boolean isCoinsuranceRecommended;
    private String itemizedScores;
    private float totalScore;
    private String underwriter;
    private UnderwritingTerm underwritingItem;
    private String underwritingOpinion;
    private UnderwritingStatus underwritingStatus;
    private UnderwritingType underwritingType;
    private LocalDateTime underwrittenAt;

    // 심사 점수 계산
    public float calculateScore() { return totalScore; }

    // 자동 심사 실행
    public void executeAutoUnderwriting() {}

    // 심사 보고서 출력
    public void printReport() {}

    // 수동 심사 등록
    public void registerManualUnderwriting() {}

    // 임시 저장
    public void tempSave() {}
}
