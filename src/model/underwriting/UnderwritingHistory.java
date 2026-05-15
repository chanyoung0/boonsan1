package model.underwriting;

import enums.Gender;
import model.accident.AccidentHistory;
import model.person.InsuredPerson;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 언더라이팅 이력 도메인 모델 — 피보험자 심사 판단 결과 이력 관리
public class UnderwritingHistory {

    private InsuredPerson insuredPerson;
    private String name;
    private int age;
    private Gender gender;
    private String occupation;
    private BigDecimal annualIncome;
    private String pastMedicalHistory;
    private boolean isMedicated;
    private String surgeryHistory;
    private String familyHistory;
    private boolean isSmoker;
    private String alcoholConsumption;
    private String BMI;
    private String vehicleModel;
    private String vehicleNumber;
    private String residentRegistrationNumber;
    private LocalDateTime inquiredAt;
    private final List<UnderwritingHistory> history = new ArrayList<>();
    private final List<AccidentHistory> accidentHistories = new ArrayList<>();

    public UnderwritingHistory() {}

    // 심사 이력 기본 정보로 초기화
    public UnderwritingHistory(InsuredPerson insuredPerson, String name, int age, Gender gender, String occupation) {
        this.insuredPerson = insuredPerson;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.occupation = occupation;
        this.inquiredAt = LocalDateTime.now();
    }

    // 동일 피보험자 심사 이력 목록 조회
    public List<UnderwritingHistory> getHistory() {
        return history;
    }

    // 심사 이력 등록
    public void registerHistory() {}

    // 심사 이력 수정
    public void updateHistory() {}

    public void addHistory(UnderwritingHistory h) { this.history.add(h); }
    public List<AccidentHistory> getAccidentHistories() { return accidentHistories; }
    public void addAccidentHistory(AccidentHistory h) { this.accidentHistories.add(h); }

    public InsuredPerson getInsuredPerson() { return insuredPerson; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public Gender getGender() { return gender; }
    public String getOccupation() { return occupation; }
    public BigDecimal getAnnualIncome() { return annualIncome; }
    public String getPastMedicalHistory() { return pastMedicalHistory; }
    public boolean isMedicated() { return isMedicated; }
    public String getSurgeryHistory() { return surgeryHistory; }
    public String getFamilyHistory() { return familyHistory; }
    public boolean isSmoker() { return isSmoker; }
    public String getAlcoholConsumption() { return alcoholConsumption; }
    public String getBMI() { return BMI; }
    public String getVehicleModel() { return vehicleModel; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getResidentRegistrationNumber() { return residentRegistrationNumber; }
    public LocalDateTime getInquiredAt() { return inquiredAt; }

    public void setInsuredPerson(InsuredPerson p) { this.insuredPerson = p; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setOccupation(String s) { this.occupation = s; }
    public void setAnnualIncome(BigDecimal v) { this.annualIncome = v; }
    public void setPastMedicalHistory(String s) { this.pastMedicalHistory = s; }
    public void setMedicated(boolean b) { this.isMedicated = b; }
    public void setSurgeryHistory(String s) { this.surgeryHistory = s; }
    public void setFamilyHistory(String s) { this.familyHistory = s; }
    public void setSmoker(boolean b) { this.isSmoker = b; }
    public void setAlcoholConsumption(String s) { this.alcoholConsumption = s; }
    public void setBMI(String s) { this.BMI = s; }
    public void setVehicleModel(String s) { this.vehicleModel = s; }
    public void setVehicleNumber(String s) { this.vehicleNumber = s; }
    public void setResidentRegistrationNumber(String s) { this.residentRegistrationNumber = s; }
    public void setInquiredAt(LocalDateTime t) { this.inquiredAt = t; }

    @Override
    public String toString() {
        return "UnderwritingHistory{name='" + name + "', age=" + age + ", inquiredAt=" + inquiredAt + "}";
    }
}
