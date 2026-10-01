package com.bankcore.account;

import java.math.BigDecimal;

public class AccountService {

    private final Account account;

    public AccountService(Account account) {
        if(account == null) {
            throw new IllegalArgumentException("account should not be null");
        }
        this.account = account;
    }

    public void deposit(BigDecimal amount) {
        account.deposit(amount);
    }

    public void withdraw(BigDecimal amount) {
        account.withdraw(amount);
    }
}
