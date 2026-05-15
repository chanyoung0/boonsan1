package model.person;

import enums.AccountType;
import enums.BankName;

import java.math.BigDecimal;

// 계좌 도메인 모델 — 보험료 자동이체 및 보험금 수령 계좌 정보 관리
public class Account {

    private String accountHolder;
    private String accountNumber;
    private AccountType accountType;
    private BankName bankName;
    private BigDecimal balance;

    public Account() {}

    public Account(String accountNumber, String accountHolder, BankName bankName,
                   AccountType accountType, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.bankName = bankName;
        this.accountType = accountType;
        this.balance = balance;
    }

    // 자동이체 해지
    public void cancelAutoTransfer() {}

    // 계좌 변경
    public void changeAccount() {}

    // 잔액 확인 — 이체 가능 여부 반환
    public boolean checkBalance() { return balance != null && balance.compareTo(BigDecimal.ZERO) > 0; }

    public String      getAccountHolder()           { return accountHolder; }
    public void        setAccountHolder(String v)   { this.accountHolder = v; }
    public String      getAccountNumber()           { return accountNumber; }
    public void        setAccountNumber(String v)   { this.accountNumber = v; }
    public AccountType getAccountType()             { return accountType; }
    public void        setAccountType(AccountType v){ this.accountType = v; }
    public BankName    getBankName()                { return bankName; }
    public void        setBankName(BankName v)      { this.bankName = v; }
    public BigDecimal  getBalance()                 { return balance; }
    public void        setBalance(BigDecimal v)     { this.balance = v; }

    @Override
    public String toString() {
        return "Account{accountNumber='" + accountNumber + "', accountHolder='" + accountHolder + "', bankName=" + bankName + "}";
    }
}
