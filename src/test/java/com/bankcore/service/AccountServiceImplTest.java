package com.bankcore.service;

import com.bankcore.account.Account;
import com.bankcore.account.AccountType;
import com.bankcore.exception.*;
import com.bankcore.transaction.Transaction;
import com.bankcore.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    private TransactionService transactionService;
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        accountService = new AccountServiceImpl(transactionService);
    }

    // ---------------- CREATE ACCOUNT TESTS ----------------

    @Test
    void createSavingsAccount_shouldCreateAccount() {
        Account account = accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        assertNotNull(account);
        assertEquals("SAV001", account.getAccountNumber());
        assertEquals("CUST001", account.getCustomerId());
        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));
        assertEquals(AccountType.SAVINGS, account.getAccountType());
    }

    @Test
    void createCurrentAccount_shouldCreateAccount() {
        Account account = accountService.createCurrentAccount(
                "CUR001", "CUST002", new BigDecimal("10000.00"));

        assertNotNull(account);
        assertEquals("CUR001", account.getAccountNumber());
        assertEquals("CUST002", account.getCustomerId());
        assertEquals(0, new BigDecimal("10000.00")
                .compareTo(account.getBalance()));
        assertEquals(AccountType.CURRENT, account.getAccountType());
    }

    @Test
    void getAccount_shouldReturnExistingAccount() {
        Account created = accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        Account retrieved = accountService.getAccount("SAV001");

        assertSame(created, retrieved);
    }

    @Test
    void getAccount_shouldThrowExceptionWhenAccountDoesNotExist() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccount("UNKNOWN"));
    }

    // ---------------- DEPOSIT TESTS ----------------

    @Test
    void deposit_shouldUpdateBalanceAndRecordTransaction() {
        Account account = accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        Transaction transaction = mock(Transaction.class);

        when(transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00")))
                .thenReturn(transaction);

        accountService.deposit("SAV001", new BigDecimal("1000.00"));

        assertEquals(0, new BigDecimal("6000.00")
                .compareTo(account.getBalance()));

        verify(transactionService).createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        verify(transactionService).recordTransaction(transaction);
    }

    @Test
    void deposit_shouldThrowExceptionForUnknownAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.deposit(
                        "UNKNOWN", new BigDecimal("500.00")));

        verifyNoInteractions(transactionService);
    }

    @Test
    void deposit_shouldNotRecordTransactionForInvalidAmount() {
        Account account = accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        assertThrows(InvalidAmountException.class,
                () -> accountService.deposit(
                        "SAV001", BigDecimal.ZERO));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));

        verifyNoInteractions(transactionService);
    }

    // ---------------- WITHDRAWAL TESTS ----------------

    @Test
    void withdraw_shouldUpdateBalanceAndRecordTransaction() {
        Account account = accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        Transaction transaction = mock(Transaction.class);

        when(transactionService.createTransaction(
                "SAV001", TransactionType.WITHDRAWAL,
                new BigDecimal("1000.00")))
                .thenReturn(transaction);

        accountService.withdraw("SAV001", new BigDecimal("1000.00"));

        assertEquals(0, new BigDecimal("4000.00")
                .compareTo(account.getBalance()));

        verify(transactionService).createTransaction(
                "SAV001", TransactionType.WITHDRAWAL,
                new BigDecimal("1000.00"));

        verify(transactionService).recordTransaction(transaction);
    }

    @Test
    void withdraw_shouldThrowExceptionWhenAccountDoesNotExist() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.withdraw(
                        "UNKNOWN", new BigDecimal("500.00")));

        verifyNoInteractions(transactionService);
    }

    @Test
    void withdraw_shouldNotRecordTransactionWhenSavingsMinimumBalanceViolated() {
        Account account = accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        assertThrows(
                MinimumBalanceException.class,
                () -> accountService.withdraw(
                        "SAV001", new BigDecimal("4500.00")));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));

        verifyNoInteractions(transactionService);
    }

    @Test
    void withdraw_shouldThrowExceptionForZeroAmount() {
        Account account = accountService.createCurrentAccount(
                "CUR001", "CUST001", new BigDecimal("5000.00"));

        assertThrows(InvalidAmountException.class,
                () -> accountService.withdraw(
                        "CUR001", BigDecimal.ZERO));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));

        verifyNoInteractions(transactionService);
    }

    // ---------------- TRANSFER TESTS ----------------

    @Test
    void transfer_shouldUpdateBalancesAndRecordTwoTransactions() {
        Account source = accountService.createCurrentAccount(
                "CUR001", "CUST001", new BigDecimal("10000.00"));

        Account destination = accountService.createSavingsAccount(
                "SAV001", "CUST002", new BigDecimal("5000.00"));

        Transaction debit = mock(Transaction.class);
        Transaction credit = mock(Transaction.class);

        when(transactionService.createTransaction(
                "CUR001", TransactionType.TRANSFER,
                new BigDecimal("2000.00")))
                .thenReturn(debit);

        when(transactionService.createTransaction(
                "SAV001", TransactionType.TRANSFER,
                new BigDecimal("2000.00")))
                .thenReturn(credit);

        accountService.transfer(
                "CUR001", "SAV001", new BigDecimal("2000.00"));

        assertEquals(0, new BigDecimal("8000.00")
                .compareTo(source.getBalance()));

        assertEquals(0, new BigDecimal("7000.00")
                .compareTo(destination.getBalance()));

        verify(transactionService).createTransaction(
                "CUR001", TransactionType.TRANSFER,
                new BigDecimal("2000.00"));

        verify(transactionService).createTransaction(
                "SAV001", TransactionType.TRANSFER,
                new BigDecimal("2000.00"));

        verify(transactionService).recordTransaction(debit);
        verify(transactionService).recordTransaction(credit);
        verify(transactionService, times(2))
                .recordTransaction(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionForSameAccount() {
        accountService.createCurrentAccount(
                "CUR001", "CUST001", new BigDecimal("5000.00"));

        assertThrows(SameAccountTransferException.class,
                () -> accountService.transfer(
                        "CUR001", "CUR001",
                        new BigDecimal("1000.00")));

        verifyNoInteractions(transactionService);
    }

    @Test
    void transfer_shouldThrowExceptionWhenSourceAccountDoesNotExist() {
        accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        assertThrows(AccountNotFoundException.class,
                () -> accountService.transfer(
                        "UNKNOWN", "SAV001",
                        new BigDecimal("1000.00")));

        verifyNoInteractions(transactionService);
    }

    @Test
    void transfer_shouldThrowExceptionWhenDestinationAccountDoesNotExist() {
        Account source = accountService.createCurrentAccount(
                "CUR001", "CUST001", new BigDecimal("5000.00"));

        assertThrows(AccountNotFoundException.class,
                () -> accountService.transfer(
                        "CUR001", "UNKNOWN",
                        new BigDecimal("1000.00")));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(source.getBalance()));

        verifyNoInteractions(transactionService);
    }

    @Test
    void transfer_shouldNotRecordTransactionsWhenWithdrawalFails() {
        Account source = accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        Account destination = accountService.createSavingsAccount(
                "SAV002", "CUST002", new BigDecimal("5000.00"));

        assertThrows(MinimumBalanceException.class,
                () -> accountService.transfer(
                        "SAV001", "SAV002",
                        new BigDecimal("4500.00")));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(source.getBalance()));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(destination.getBalance()));

        verifyNoInteractions(transactionService);
    }

    @Test
    void transfer_shouldThrowExceptionForInvalidAmount() {
        accountService.createCurrentAccount(
                "CUR001", "CUST001", new BigDecimal("5000.00"));

        accountService.createSavingsAccount(
                "SAV001", "CUST002", new BigDecimal("5000.00"));

        assertThrows(InvalidAmountException.class,
                () -> accountService.transfer(
                        "CUR001", "SAV001", BigDecimal.ZERO));

        verifyNoInteractions(transactionService);
    }

    // ---------------- TRANSACTION HISTORY TESTS ----------------

    @Test
    void getTransactions_shouldReturnAccountTransactions() {
        accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        List<Transaction> transactions = Arrays.asList(
                mock(Transaction.class),
                mock(Transaction.class));

        when(transactionService.getTransactions("SAV001"))
                .thenReturn(transactions);

        List<Transaction> result =
                accountService.getTransactions("SAV001");

        assertEquals(2, result.size());
        assertSame(transactions, result);

        verify(transactionService).getTransactions("SAV001");
    }

    @Test
    void getTransactions_shouldReturnEmptyListWhenNoTransactionsExist() {
        accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        when(transactionService.getTransactions("SAV001"))
                .thenReturn(Collections.emptyList());

        List<Transaction> result =
                accountService.getTransactions("SAV001");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactions_shouldThrowExceptionForUnknownAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.getTransactions("UNKNOWN"));

        verifyNoInteractions(transactionService);
    }
}