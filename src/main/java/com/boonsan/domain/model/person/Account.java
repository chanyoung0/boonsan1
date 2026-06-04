package com.boonsan.domain.model.person;

import com.boonsan.domain.enums.AccountType;
import com.boonsan.domain.enums.BankName;

public class Account {

    private String accountHolder;
    private String accountNumber;
    private AccountType accountType;
    private BankName bankName;

    public void cancelAutoTransfer() {}

    public void changeAccount() {}

    public void checkBalance() {}
}
