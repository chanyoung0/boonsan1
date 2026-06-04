package com.boonsan.domain.contract.dto;

import com.boonsan.domain.enums.ContractStatus;
import com.boonsan.domain.enums.PaymentCycle;
import com.boonsan.domain.model.contract.Contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContractResponse {

    private final String policyNumber;
    private final String productCode;
    private final ContractStatus contractStatus;
    private final PaymentCycle paymentCycle;
    private final BigDecimal premiumAmount;
    private final int installmentCount;
    private final Boolean hasUnpaidPremium;
    private final LocalDate contractStartDate;
    private final LocalDate contractEndDate;
    private final String insuredName;
    private final String insuredRrn;
    private final String insuredContact;
    private final String accountNumber;
    private final String accountBank;
    private final LocalDateTime createdAt;

    private ContractResponse(
            String policyNumber,
            String productCode,
            ContractStatus contractStatus,
            PaymentCycle paymentCycle,
            BigDecimal premiumAmount,
            int installmentCount,
            Boolean hasUnpaidPremium,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            String insuredName,
            String insuredRrn,
            String insuredContact,
            String accountNumber,
            String accountBank,
            LocalDateTime createdAt
    ) {
        this.policyNumber = policyNumber;
        this.productCode = productCode;
        this.contractStatus = contractStatus;
        this.paymentCycle = paymentCycle;
        this.premiumAmount = premiumAmount;
        this.installmentCount = installmentCount;
        this.hasUnpaidPremium = hasUnpaidPremium;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.insuredName = insuredName;
        this.insuredRrn = insuredRrn;
        this.insuredContact = insuredContact;
        this.accountNumber = accountNumber;
        this.accountBank = accountBank;
        this.createdAt = createdAt;
    }

    public static ContractResponse from(Contract contract) {
        return new ContractResponse(
                contract.getPolicyNumber(),
                contract.getProductCode(),
                contract.getContractStatus(),
                contract.getPaymentCycle(),
                contract.getPremiumAmount(),
                contract.getInstallmentCount(),
                contract.getHasUnpaidPremium(),
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                contract.getInsuredName(),
                maskRrn(contract.getInsuredRrn()),
                contract.getInsuredContact(),
                maskAccountNumber(contract.getAccountNumber()),
                contract.getAccountBank(),
                contract.getCreatedAt()
        );
    }

    // 주민등록번호 뒷자리 마스킹 (LLM_WIKI §10: 민감 정보는 마스킹 후 반환)
    private static String maskRrn(String rrn) {
        if (rrn == null) return null;
        int dashIdx = rrn.indexOf('-');
        if (dashIdx < 0 || dashIdx + 2 > rrn.length()) return rrn;
        String front = rrn.substring(0, dashIdx);
        String firstDigit = rrn.substring(dashIdx + 1, Math.min(dashIdx + 2, rrn.length()));
        return front + "-" + firstDigit + "******";
    }

    private static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 5) return accountNumber;
        int visible = 4;
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < accountNumber.length() - visible; i++) {
            char c = accountNumber.charAt(i);
            masked.append(c == '-' ? '-' : '*');
        }
        masked.append(accountNumber.substring(accountNumber.length() - visible));
        return masked.toString();
    }

    public String getPolicyNumber() { return policyNumber; }

    public String getProductCode() { return productCode; }

    public ContractStatus getContractStatus() { return contractStatus; }

    public PaymentCycle getPaymentCycle() { return paymentCycle; }

    public BigDecimal getPremiumAmount() { return premiumAmount; }

    public int getInstallmentCount() { return installmentCount; }

    public Boolean getHasUnpaidPremium() { return hasUnpaidPremium; }

    public LocalDate getContractStartDate() { return contractStartDate; }

    public LocalDate getContractEndDate() { return contractEndDate; }

    public String getInsuredName() { return insuredName; }

    public String getInsuredRrn() { return insuredRrn; }

    public String getInsuredContact() { return insuredContact; }

    public String getAccountNumber() { return accountNumber; }

    public String getAccountBank() { return accountBank; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
