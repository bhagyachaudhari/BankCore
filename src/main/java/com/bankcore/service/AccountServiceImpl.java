package com.bankcore.service;

import com.bankcore.account.Account;
import com.bankcore.account.SavingsAccount;
import com.bankcore.exception.AccountNotFoundException;
import com.bankcore.exception.SameAccountTransferException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {

    private final Map<String, Account> accounts = new HashMap<>();

    @Override
    public Account createSavingsAccount(String accountNumber, String customerId, BigDecimal initialBalance) {

        Account savings = new SavingsAccount(
                accountNumber, customerId, initialBalance);

        accounts.put(accountNumber, savings);

        return savings;
    }

    @Override
    public Account createCurrentAccount(String accountNumber, String customerId, BigDecimal initialBalance) {
        return null;
    }

    @Override
    public Account getAccount(String accountNumber) {

        Account account = accounts.get(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException("Account Not Found.");
        }

        return account;
    }

    @Override
    public void deposit(String accountNumber, BigDecimal amount) {
        getAccount(accountNumber).deposit(amount);
    }

    @Override
    public void withdraw(String accountNumber, BigDecimal amount) {
        getAccount(accountNumber).withdraw(amount);
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new SameAccountTransferException(
                    "Source and destination accounts cannot be the same");
        }

        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }
}
