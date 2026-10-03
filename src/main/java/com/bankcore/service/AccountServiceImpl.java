package com.bankcore.service;

import com.bankcore.account.Account;
import com.bankcore.account.CurrentAccount;
import com.bankcore.account.SavingsAccount;
import com.bankcore.exception.AccountAlreadyExistsException;
import com.bankcore.exception.AccountNotFoundException;
import com.bankcore.exception.SameAccountTransferException;
import com.bankcore.transaction.Transaction;
import com.bankcore.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AccountServiceImpl implements AccountService {

    private final Map<String, Account> accounts = new HashMap<>();
    private final TransactionService transactionService;
    private final CustomerService customerService;

    public AccountServiceImpl(TransactionService transactionService,
                              CustomerService customerService) {
        this.transactionService = Objects.requireNonNull(
                transactionService, "transactionService cannot be null");
        this.customerService = Objects.requireNonNull(
                customerService, "customerService cannot be null");
    }

    @Override
    public Account createSavingsAccount(String accountNumber,
                                        String customerId,
                                        BigDecimal initialBalance) {
        validateNewAccount(accountNumber, customerId);

        Account savings = new SavingsAccount(
                accountNumber, customerId, initialBalance);

        accounts.put(accountNumber, savings);
        return savings;
    }

    @Override
    public Account createCurrentAccount(String accountNumber,
                                        String customerId,
                                        BigDecimal initialBalance) {
        validateNewAccount(accountNumber, customerId);

        Account current = new CurrentAccount(
                accountNumber, customerId, initialBalance);

        accounts.put(accountNumber, current);
        return current;
    }

    private void validateNewAccount(String accountNumber,
                                    String customerId) {
        Objects.requireNonNull(accountNumber,
                "accountNumber cannot be null");
        Objects.requireNonNull(customerId,
                "customerId cannot be null");

        if (accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "accountNumber cannot be blank");
        }

        if (accounts.containsKey(accountNumber)) {
            throw new AccountAlreadyExistsException(
                    "Account already exists: " + accountNumber);
        }

        // Throws CustomerNotFoundException if customer is missing.
        customerService.getCustomer(customerId);
    }

    @Override
    public Account getAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(
                    "Account Not Found: " + accountNumber);
        }

        return account;
    }

    @Override
    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        account.deposit(amount);

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
    public void transfer(String fromAccountNumber,
                         String toAccountNumber,
                         BigDecimal amount) {
        if (Objects.equals(fromAccountNumber, toAccountNumber)) {
            throw new SameAccountTransferException(
                    "Source and destination accounts cannot be the same");
        }

        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

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
        getAccount(accountNumber);
        return transactionService.getTransactions(accountNumber);
    }
}
