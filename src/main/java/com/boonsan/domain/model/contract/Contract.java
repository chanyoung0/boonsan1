package com.boonsan.domain.model.contract;

import com.boonsan.domain.enums.ContractStatus;
import com.boonsan.domain.enums.PaymentCycle;
import com.boonsan.domain.model.person.Account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 보험 계약 도메인 모델 — 증권발행 완료 후 생성되는 핵심 계약 엔티티
public class Contract {

    private Account autoTransferAmount;
    private ContractStatus contractStatus;
    private PaymentCycle paymentCycle;
    private Boolean hasUnpaidPremium;
    private int installmentCount;
    private String policyNumber;
    private String productCode;
    private BigDecimal premiumAmount;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String insuredName;
    private String insuredRrn;
    private String insuredContact;
    private String accountNumber;
    private String accountBank;
    private LocalDateTime createdAt;

    public Contract() {
    }

    // 계약 상태 변경
    public void changeContractStatus() {}

    // 납입 상태 확인
    public boolean checkPaymentStatus() {
        return !Boolean.TRUE.equals(hasUnpaidPremium);
    }

    // 계약 실행
    public void executeContract() {}

    // 계약 정보 조회
    public void getContractInfo() {}

    // 증권번호 발행
    public void issuePolicyNumber() {}

    // 계약 갱신
    public void renewContract() {}

    // 계약 종료
    public void terminateContract() {}

    public String getPolicyNumber() { return policyNumber; }

    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public ContractStatus getContractStatus() { return contractStatus; }

    public void setContractStatus(ContractStatus contractStatus) { this.contractStatus = contractStatus; }

    public PaymentCycle getPaymentCycle() { return paymentCycle; }

    public void setPaymentCycle(PaymentCycle paymentCycle) { this.paymentCycle = paymentCycle; }

    public Boolean getHasUnpaidPremium() { return hasUnpaidPremium; }

    public void setHasUnpaidPremium(Boolean hasUnpaidPremium) { this.hasUnpaidPremium = hasUnpaidPremium; }

    public int getInstallmentCount() { return installmentCount; }

    public void setInstallmentCount(int installmentCount) { this.installmentCount = installmentCount; }

    public String getProductCode() { return productCode; }

    public void setProductCode(String productCode) { this.productCode = productCode; }

    public BigDecimal getPremiumAmount() { return premiumAmount; }

    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }

    public LocalDate getContractStartDate() { return contractStartDate; }

    public void setContractStartDate(LocalDate contractStartDate) { this.contractStartDate = contractStartDate; }

    public LocalDate getContractEndDate() { return contractEndDate; }

    public void setContractEndDate(LocalDate contractEndDate) { this.contractEndDate = contractEndDate; }

    public String getInsuredName() { return insuredName; }

    public void setInsuredName(String insuredName) { this.insuredName = insuredName; }

    public String getInsuredRrn() { return insuredRrn; }

    public void setInsuredRrn(String insuredRrn) { this.insuredRrn = insuredRrn; }

    public String getInsuredContact() { return insuredContact; }

    public void setInsuredContact(String insuredContact) { this.insuredContact = insuredContact; }

    public String getAccountNumber() { return accountNumber; }

    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountBank() { return accountBank; }

    public void setAccountBank(String accountBank) { this.accountBank = accountBank; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
