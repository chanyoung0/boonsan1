package com.boonsan.domain.underwriting.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UnderwritingApplicationResponse {

    private String applicationId;
    private String applicationStatus;
    private LocalDateTime appliedAt;
    private String appliedCondition;
    private BigDecimal insuredAmount;
    private String insuredPersonInfo;
    private String paymentCycle;
    private BigDecimal premium;
    private String productCode;
    private String specialContractList;
    private String termsVersion;
    private String insuredPersonName;
    private Integer age;
    private String gender;
    private String occupation;
    private BigDecimal annualIncome;
    private String pastMedicalHistory;
    private boolean medicated;
    private String surgeryHistory;
    private String familyHistory;
    private boolean smoker;
    private String alcoholConsumption;
    private BigDecimal bmi;
    private String vehicleModel;
    private String vehicleNumber;
    private boolean hasAccidentHistory;
    private boolean hasOtherContract;
    private String nextStepMessage;

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public String getAppliedCondition() { return appliedCondition; }
    public void setAppliedCondition(String appliedCondition) { this.appliedCondition = appliedCondition; }
    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }
    public String getInsuredPersonInfo() { return insuredPersonInfo; }
    public void setInsuredPersonInfo(String insuredPersonInfo) { this.insuredPersonInfo = insuredPersonInfo; }
    public String getPaymentCycle() { return paymentCycle; }
    public void setPaymentCycle(String paymentCycle) { this.paymentCycle = paymentCycle; }
    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getSpecialContractList() { return specialContractList; }
    public void setSpecialContractList(String specialContractList) { this.specialContractList = specialContractList; }
    public String getTermsVersion() { return termsVersion; }
    public void setTermsVersion(String termsVersion) { this.termsVersion = termsVersion; }
    public String getInsuredPersonName() { return insuredPersonName; }
    public void setInsuredPersonName(String insuredPersonName) { this.insuredPersonName = insuredPersonName; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public BigDecimal getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }
    public String getPastMedicalHistory() { return pastMedicalHistory; }
    public void setPastMedicalHistory(String pastMedicalHistory) { this.pastMedicalHistory = pastMedicalHistory; }
    public boolean isMedicated() { return medicated; }
    public void setMedicated(boolean medicated) { this.medicated = medicated; }
    public String getSurgeryHistory() { return surgeryHistory; }
    public void setSurgeryHistory(String surgeryHistory) { this.surgeryHistory = surgeryHistory; }
    public String getFamilyHistory() { return familyHistory; }
    public void setFamilyHistory(String familyHistory) { this.familyHistory = familyHistory; }
    public boolean isSmoker() { return smoker; }
    public void setSmoker(boolean smoker) { this.smoker = smoker; }
    public String getAlcoholConsumption() { return alcoholConsumption; }
    public void setAlcoholConsumption(String alcoholConsumption) { this.alcoholConsumption = alcoholConsumption; }
    public BigDecimal getBmi() { return bmi; }
    public void setBmi(BigDecimal bmi) { this.bmi = bmi; }
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public boolean isHasAccidentHistory() { return hasAccidentHistory; }
    public void setHasAccidentHistory(boolean hasAccidentHistory) { this.hasAccidentHistory = hasAccidentHistory; }
    public boolean isHasOtherContract() { return hasOtherContract; }
    public void setHasOtherContract(boolean hasOtherContract) { this.hasOtherContract = hasOtherContract; }
    public String getNextStepMessage() { return nextStepMessage; }
    public void setNextStepMessage(String nextStepMessage) { this.nextStepMessage = nextStepMessage; }
}
