package model.underwriting;

import enums.Gender;

import java.math.BigDecimal;

// 언더라이팅 이력 도메인 모델 — 피보험자 심사 판단 결과 이력 관리
public class UnderwritingHistory {

    private int age;
    private String alcoholConsumption;
    private BigDecimal annualIncome;
    private float BMI;
    private String familyHistory;
    private Gender gender;
    private boolean isApproved;
    private boolean isMedicated;
    private boolean isSmoker;
    private boolean isVAMedicated;
    private String name;
    private String occupationType;
    private String pastMediaHistory;
    private String residentialRegistrationNumber;
    private String surgeryHistory;
    private String vehicleModel;
    private String vehicleNumber;

    // 심사 이력 조회
    public void getHistory() {}

    // 심사 이력 등록
    public void registerHistory() {}

    // 심사 이력 수정
    public void updateHistory() {}
}
