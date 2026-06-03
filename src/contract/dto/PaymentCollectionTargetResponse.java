package contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentCollectionTargetResponse {

    private String policyNumber;
    private String insuredName;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal plannedAmount;
    private String accountNumber;
    private String accountBank;

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getInsuredName() { return insuredName; }
    public void setInsuredName(String insuredName) { this.insuredName = insuredName; }

    public Integer getInstallmentNo() { return installmentNo; }
    public void setInstallmentNo(Integer installmentNo) { this.installmentNo = installmentNo; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getPlannedAmount() { return plannedAmount; }
    public void setPlannedAmount(BigDecimal plannedAmount) { this.plannedAmount = plannedAmount; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountBank() { return accountBank; }
    public void setAccountBank(String accountBank) { this.accountBank = accountBank; }
}
