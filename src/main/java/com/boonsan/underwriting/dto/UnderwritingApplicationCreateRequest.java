package com.boonsan.underwriting.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UnderwritingApplicationCreateRequest {

    @NotBlank
    private String productCode;
    @NotNull
    @DecimalMin("1")
    private BigDecimal insuredAmount;
    @NotNull
    @DecimalMin("0")
    private BigDecimal premium;
    @NotBlank
    private String paymentCycle;
    @NotBlank
    private String termsVersion;
    private String specialContractList;
    private String appliedCondition;
    @NotBlank
    private String insuredPersonName;
    @NotNull
    @Min(0)
    private Integer age;
    @NotBlank
    private String gender;
    @NotBlank
    private String occupation;
    @NotNull
    @DecimalMin("0")
    private BigDecimal annualIncome;
    private String pastMedicalHistory;
    private boolean medicated;
    private String surgeryHistory;
    private String familyHistory;
    private boolean smoker;
    private String alcoholConsumption;
    @NotNull
    @DecimalMin("0")
    private BigDecimal bmi;
    private String vehicleModel;
    private String vehicleNumber;
    private boolean hasAccidentHistory;
    private boolean hasOtherContract;

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public BigDecimal getInsuredAmount() { return insuredAmount; }
    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }
    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }
    public String getPaymentCycle() { return paymentCycle; }
    public void setPaymentCycle(String paymentCycle) { this.paymentCycle = paymentCycle; }
    public String getTermsVersion() { return termsVersion; }
    public void setTermsVersion(String termsVersion) { this.termsVersion = termsVersion; }
    public String getSpecialContractList() { return specialContractList; }
    public void setSpecialContractList(String specialContractList) { this.specialContractList = specialContractList; }
    public String getAppliedCondition() { return appliedCondition; }
    public void setAppliedCondition(String appliedCondition) { this.appliedCondition = appliedCondition; }
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
}
