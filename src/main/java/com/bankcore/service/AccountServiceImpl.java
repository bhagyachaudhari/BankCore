package com.bankcore.service;

import com.bankcore.account.Account;
import com.bankcore.account.CurrentAccount;
import com.bankcore.account.SavingsAccount;
import com.bankcore.exception.AccountNotFoundException;
import com.bankcore.exception.SameAccountTransferException;
import com.bankcore.transaction.Transaction;
import com.bankcore.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountServiceImpl implements AccountService {

    private final Map<String, Account> accounts = new HashMap<>();
    private final TransactionService transactionService;

    public AccountServiceImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public Account createSavingsAccount(String accountNumber, String customerId, BigDecimal initialBalance) {

        Account savings = new SavingsAccount(
                accountNumber, customerId, initialBalance);

        accounts.put(accountNumber, savings);

        return savings;
    }

    @Override
    public Account createCurrentAccount(String accountNumber, String customerId, BigDecimal initialBalance) {
        Account current = new CurrentAccount(
                accountNumber, customerId, initialBalance);

        accounts.put(accountNumber, current);
        return current;
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

        Account account = getAccount(accountNumber);

        // Perform the account operation first.
        account.deposit(amount);

        // Record history only if the operation succeeds.
        Transaction transaction = transactionService.createTransaction(
                accountNumber, TransactionType.DEPOSIT, amount);

        transactionService.recordTransaction(transaction);
    }

    @Override
    public void withdraw(String accountNumber, BigDecimal amount) {

        Account account = getAccount(accountNumber);

        account.withdraw(amount);

        Transaction transaction = transactionService.createTransaction(
                accountNumber, TransactionType.WITHDRAWAL, amount);

        transactionService.recordTransaction(transaction);
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new SameAccountTransferException(
                    "Source and destination accounts cannot be the same");
        }

        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

        // Apply both account operations before recording history.
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        Transaction debit = transactionService.createTransaction(
                fromAccountNumber, TransactionType.TRANSFER, amount);

        Transaction credit = transactionService.createTransaction(
                toAccountNumber, TransactionType.TRANSFER, amount);

        transactionService.recordTransaction(debit);
        transactionService.recordTransaction(credit);
    }

    @Override
    public List<Transaction> getTransactions(String accountNumber) {
        getAccount(accountNumber); // Ensure the account exists.
        return transactionService.getTransactions(accountNumber);
    }
}
