package com.bankcore.account;

import com.bankcore.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Account {

    private final String accountNumber;
    private final String customerId;
    private BigDecimal balance;

    public Account(String accountNumber,
                   String customerId,
                   BigDecimal initialBalance) {

        Objects.requireNonNull(accountNumber, "accountNumber cannot be null");
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(initialBalance, "initialBalance cannot be null");

        if (accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "accountNumber cannot be empty");
        }

        if (customerId.isBlank()) {
            throw new IllegalArgumentException(
                    "customerId cannot be empty");
        }

        if (initialBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "initialBalance must be greater than zero");
        }

        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = initialBalance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        validatePositiveAmount(amount, "Deposit");

        balance = balance.add(amount);
    }

    protected void validatePositiveAmount(BigDecimal amount,
                                          String operation) {
        Objects.requireNonNull(amount, operation + " amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    operation + " amount must be greater than zero");
        }
    }

    protected void subtractFromBalance(BigDecimal amount) {

        balance = balance.subtract(amount);
    }

    public abstract void withdraw(BigDecimal amount);

    public abstract AccountType getAccountType();

}
