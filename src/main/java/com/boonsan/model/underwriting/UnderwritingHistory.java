package com.boonsan.model.underwriting;

import com.boonsan.enums.Gender;
import com.boonsan.model.person.InsuredPerson;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 언더라이팅 이력 도메인 모델 — 피보험자 심사 판단 결과 이력 관리
public class UnderwritingHistory {

    private int age;
    private String alcoholConsumption;
    private BigDecimal annualIncome;
    private String BMI;
    private String familyHistory;
    private Gender gender;
    private LocalDateTime inquiredAt;
    private InsuredPerson insuredPerson;
    private boolean isMedicated;
    private boolean isSmoker;
    private String name;
    private String occupation;
    private String pastMedicalHistory;
    private String residentRegistrationNumber;
    private String surgeryHistory;
    private String vehicleModel;
    private String vehicleNumber;

    // 심사 이력 조회
    public List<UnderwritingHistory> getHistory() { return List.of(this); }

    // 심사 이력 등록
    public void registerHistory() {}

    // 심사 이력 수정
    public void updateHistory() {}
}
