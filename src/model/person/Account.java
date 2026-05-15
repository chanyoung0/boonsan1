package model.person;

import enums.AccountType;
import enums.BankName;

import java.math.BigDecimal;

// 계좌 도메인 모델 — 보험료 납입 및 보험금 지급용 계좌 정보 관리
public class Account {

    private String accountHolder;
    private String accountNumber;
    private AccountType accountType;
    private BankName bankName;
    private BigDecimal balance;

    public Account() {}

    // 계좌 기본 정보로 초기화
    public Account(String accountHolder, String accountNumber, AccountType accountType, BankName bankName, BigDecimal balance) {
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.bankName = bankName;
        this.balance = balance;
    }

    // 자동이체 해지
    public void cancelAutoTransfer() {}

    // 계좌 변경
    public void changeAccount() {}

    // 잔액이 0보다 큰지 확인
    public boolean checkBalance() {
        return balance != null && balance.signum() > 0;
    }

    public String getAccountHolder() { return accountHolder; }
    public String getAccountNumber() { return accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public BankName getBankName() { return bankName; }
    public BigDecimal getBalance() { return balance; }

    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public void setBankName(BankName bankName) { this.bankName = bankName; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Account{holder='" + accountHolder + "', no='" + accountNumber
                + "', bank=" + bankName + ", balance=" + balance + "}";
    }
}
