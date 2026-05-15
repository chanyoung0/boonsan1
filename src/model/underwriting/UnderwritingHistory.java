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

    private List<AccidentHistory> accidentHistoryList;

    public UnderwritingHistory() {
        this.accidentHistoryList = new ArrayList<>();
    }

    public UnderwritingHistory(String name, int age, Gender gender, String occupation,
                               BigDecimal annualIncome, InsuredPerson insuredPerson,
                               LocalDateTime inquiredAt) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.occupation = occupation;
        this.annualIncome = annualIncome;
        this.insuredPerson = insuredPerson;
        this.inquiredAt = inquiredAt;
        this.accidentHistoryList = new ArrayList<>();
    }

    // 심사 이력 목록 조회
    public List<UnderwritingHistory> getHistory() { return new ArrayList<>(); }

    // 심사 이력 등록
    public void registerHistory() {}

    // 심사 이력 수정
    public void updateHistory() {}

    public int                    getAge()                              { return age; }
    public void                   setAge(int v)                        { this.age = v; }
    public String                 getAlcoholConsumption()              { return alcoholConsumption; }
    public void                   setAlcoholConsumption(String v)      { this.alcoholConsumption = v; }
    public BigDecimal             getAnnualIncome()                    { return annualIncome; }
    public void                   setAnnualIncome(BigDecimal v)        { this.annualIncome = v; }
    public String                 getBMI()                             { return BMI; }
    public void                   setBMI(String v)                     { this.BMI = v; }
    public String                 getFamilyHistory()                   { return familyHistory; }
    public void                   setFamilyHistory(String v)           { this.familyHistory = v; }
    public Gender                 getGender()                          { return gender; }
    public void                   setGender(Gender v)                  { this.gender = v; }
    public LocalDateTime          getInquiredAt()                      { return inquiredAt; }
    public void                   setInquiredAt(LocalDateTime v)       { this.inquiredAt = v; }
    public InsuredPerson          getInsuredPerson()                   { return insuredPerson; }
    public void                   setInsuredPerson(InsuredPerson v)    { this.insuredPerson = v; }
    public boolean                isMedicated()                        { return isMedicated; }
    public void                   setMedicated(boolean v)              { this.isMedicated = v; }
    public boolean                isSmoker()                           { return isSmoker; }
    public void                   setSmoker(boolean v)                 { this.isSmoker = v; }
    public String                 getName()                            { return name; }
    public void                   setName(String v)                    { this.name = v; }
    public String                 getOccupation()                      { return occupation; }
    public void                   setOccupation(String v)              { this.occupation = v; }
    public String                 getPastMedicalHistory()              { return pastMedicalHistory; }
    public void                   setPastMedicalHistory(String v)      { this.pastMedicalHistory = v; }
    public String                 getResidentRegistrationNumber()      { return residentRegistrationNumber; }
    public void                   setResidentRegistrationNumber(String v){ this.residentRegistrationNumber = v; }
    public String                 getSurgeryHistory()                  { return surgeryHistory; }
    public void                   setSurgeryHistory(String v)          { this.surgeryHistory = v; }
    public String                 getVehicleModel()                    { return vehicleModel; }
    public void                   setVehicleModel(String v)            { this.vehicleModel = v; }
    public String                 getVehicleNumber()                   { return vehicleNumber; }
    public void                   setVehicleNumber(String v)           { this.vehicleNumber = v; }
    public List<AccidentHistory>  getAccidentHistoryList()             { return accidentHistoryList; }
    public void                   setAccidentHistoryList(List<AccidentHistory> v){ this.accidentHistoryList = v; }

    @Override
    public String toString() {
        return "UnderwritingHistory{name='" + name + "', age=" + age + ", gender=" + gender + "}";
    }
}
