package com.bankcore.service;

import com.bankcore.account.Account;
import com.bankcore.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

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

    List<Transaction> getTransactions(String accountNumber);

    void freezeAccount(String accountNumber);

    void reactivateAccount(String accountNumber);


}
