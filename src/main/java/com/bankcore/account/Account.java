package com.bankcore.account;

import com.bankcore.exception.AccountFrozenException;
import com.bankcore.exception.InvalidAmountException;
import com.bankcore.exception.InvalidAccountStateException;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Account {

    private final String accountNumber;
    private final String customerId;
    private BigDecimal balance;
    private AccountStatus status;

    public Account(String accountNumber,
                   String customerId,
                   BigDecimal initialBalance) {

        Objects.requireNonNull(accountNumber,
                "accountNumber cannot be null");
        Objects.requireNonNull(customerId,
                "customerId cannot be null");
        Objects.requireNonNull(initialBalance,
                "initialBalance cannot be null");

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
        this.status = AccountStatus.ACTIVE;
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

    public AccountStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public void freeze() {
        if (status == AccountStatus.FROZEN) {
            throw new InvalidAccountStateException(
                    "Account is already frozen: " + accountNumber);
        }

        status = AccountStatus.FROZEN;
    }

    public void reactivate() {
        if (status == AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException(
                    "Account is already active: " + accountNumber);
        }

        status = AccountStatus.ACTIVE;
    }

    public void deposit(BigDecimal amount) {
        validateActive();
        validatePositiveAmount(amount, "Deposit");
        balance = balance.add(amount);
    }

    protected void validateActive() {
        if (!isActive()) {
            throw new AccountFrozenException(
                    "Account is frozen: " + accountNumber);
        }
    }

    protected void validatePositiveAmount(BigDecimal amount,
                                          String operation) {
        Objects.requireNonNull(amount,
                operation + " amount cannot be null");

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