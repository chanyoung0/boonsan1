package model.underwriting;

import enums.PolicyAge;
import enums.UnderwritingTerm;

// 언더라이팅 도메인 모델 — 청약 위험도 평가 및 인수 심사 정보 관리
public class Underwriting {

    private boolean isCoinsuranceRecommended;
    private String itemsScored;
    private PolicyAge policyAge;
    private String underwritingOpinion;
    private UnderwritingTerm underwritingTerm;

    // 심사 점수 계산
    public void calculateScore() {}

    // 자동 심사 실행
    public void executeAutoUnderwriting() {}

    // 심사 보고서 출력
    public void printReport() {}

    // 수동 심사 등록
    public void registerManualUnderwriting() {}

    // 임시 저장
    public void tempSave() {}
}
