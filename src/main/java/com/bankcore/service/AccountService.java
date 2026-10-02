package com.bankcore.service;

import com.bankcore.account.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account createSavingsAccount(
            String accountNumber,
            String customerId,
            BigDecimal initialBalance);

    Account createCurrentAccount(
            String accountNumber,
            String customerId,
            BigDecimal initialBalance);

    Account getAccount(String accountNumber);

    void deposit(String accountNumber, BigDecimal amount);

    void withdraw(String accountNumber, BigDecimal amount);

    void transfer(
            String fromAccountNumber,
            String toAccountNumber,
            BigDecimal amount);

}
