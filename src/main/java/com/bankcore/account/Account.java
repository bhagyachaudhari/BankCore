package com.bankcore.account;

import java.math.BigDecimal;

public class Account {

    private final String accountNumber;
    private final String customerId;
    private final AccountType accountType;
    private BigDecimal balance;

    public Account(String accountNumber, String customerId, AccountType accountType, BigDecimal balance) {

        if(balance == null || balance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("balance should not be null or negative");
        }

        if(accountNumber == null || accountNumber.equals("")) {
            throw new IllegalArgumentException("accountNumber should not be null or empty");
        }

        if(accountType == null || accountType.equals("")) {
            throw new IllegalArgumentException("accountType should not be null or empty");
        }

        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);

        if (amount.compareTo(this.balance) > 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        balance = balance.subtract(amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
    }

}
