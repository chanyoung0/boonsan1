package claim.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DamageAssessmentRequest {

    @NotBlank
    @Size(max = 50)
    private String accidentNumber;

    @NotBlank
    @Size(max = 50)
    private String adjusterId;

    @NotNull
    private LocalDateTime investigationAt;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal medicalExpense;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal lostIncome;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal repairCost;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal settlementAmount;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Float faultRatio;

    public String getAccidentNumber() { return accidentNumber; }

    public void setAccidentNumber(String accidentNumber) { this.accidentNumber = accidentNumber; }

    public String getAdjusterId() { return adjusterId; }

    public void setAdjusterId(String adjusterId) { this.adjusterId = adjusterId; }

    public LocalDateTime getInvestigationAt() { return investigationAt; }

    public void setInvestigationAt(LocalDateTime investigationAt) { this.investigationAt = investigationAt; }

    public BigDecimal getMedicalExpense() { return medicalExpense; }

    public void setMedicalExpense(BigDecimal medicalExpense) { this.medicalExpense = medicalExpense; }

    public BigDecimal getLostIncome() { return lostIncome; }

    public void setLostIncome(BigDecimal lostIncome) { this.lostIncome = lostIncome; }

    public BigDecimal getRepairCost() { return repairCost; }

    public void setRepairCost(BigDecimal repairCost) { this.repairCost = repairCost; }

    public BigDecimal getSettlementAmount() { return settlementAmount; }

    public void setSettlementAmount(BigDecimal settlementAmount) { this.settlementAmount = settlementAmount; }

    public Float getFaultRatio() { return faultRatio; }

    public void setFaultRatio(Float faultRatio) { this.faultRatio = faultRatio; }
}
