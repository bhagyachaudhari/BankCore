package com.bankcore.service;

import com.bankcore.transaction.Transaction;
import com.bankcore.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceImplTest {

    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl();
    }

    // -------- CREATE TRANSACTION TESTS --------

    @Test
    void createTransaction_shouldCreateValidTransaction() {
        Transaction transaction = transactionService.createTransaction(
                "SAV001",
                TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        assertNotNull(transaction);
        assertNotNull(transaction.getTransactionId());
        assertFalse(transaction.getTransactionId().isBlank());
        assertEquals("SAV001", transaction.getAccountNumber());
        assertEquals(TransactionType.DEPOSIT,
                transaction.getTransactionType());
        assertEquals(0, new BigDecimal("1000.00")
                .compareTo(transaction.getAmount()));
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void createTransaction_shouldGenerateUniqueTransactionIds() {
        Transaction first = transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        Transaction second = transactionService.createTransaction(
                "SAV001", TransactionType.WITHDRAWAL,
                new BigDecimal("500.00"));

        assertNotEquals(
                first.getTransactionId(),
                second.getTransactionId());
    }

    @Test
    void createTransaction_shouldSetTimestamp() {
        LocalDateTime before = LocalDateTime.now();

        Transaction transaction = transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(transaction.getTimestamp());
        assertFalse(transaction.getTimestamp().isBefore(before));
        assertFalse(transaction.getTimestamp().isAfter(after));
    }

    // -------- RECORD TRANSACTION TESTS --------

    @Test
    void recordTransaction_shouldStoreTransaction() {
        Transaction transaction = transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        transactionService.recordTransaction(transaction);

        List<Transaction> result =
                transactionService.getTransactions("SAV001");

        assertEquals(1, result.size());
        assertSame(transaction, result.get(0));
    }

    @Test
    void recordTransaction_shouldStoreMultipleTransactions() {
        Transaction first = transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        Transaction second = transactionService.createTransaction(
                "SAV001", TransactionType.WITHDRAWAL,
                new BigDecimal("500.00"));

        transactionService.recordTransaction(first);
        transactionService.recordTransaction(second);

        List<Transaction> result =
                transactionService.getTransactions("SAV001");

        assertEquals(2, result.size());
        assertEquals(first, result.get(0));
        assertEquals(second, result.get(1));
    }

    @Test
    void recordTransaction_shouldThrowExceptionForNullTransaction() {
        assertThrows(NullPointerException.class,
                () -> transactionService.recordTransaction(null));
    }

    // -------- GET TRANSACTIONS TESTS --------

    @Test
    void getTransactions_shouldReturnOnlyMatchingAccountTransactions() {
        Transaction first = transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        Transaction second = transactionService.createTransaction(
                "SAV002", TransactionType.DEPOSIT,
                new BigDecimal("2000.00"));

        Transaction third = transactionService.createTransaction(
                "SAV001", TransactionType.WITHDRAWAL,
                new BigDecimal("300.00"));

        transactionService.recordTransaction(first);
        transactionService.recordTransaction(second);
        transactionService.recordTransaction(third);

        List<Transaction> result =
                transactionService.getTransactions("SAV001");

        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(third));
        assertFalse(result.contains(second));
    }

    @Test
    void getTransactions_shouldReturnEmptyListWhenNoTransactionsExist() {
        List<Transaction> result =
                transactionService.getTransactions("SAV001");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactions_shouldReturnEmptyListForUnknownAccount() {
        Transaction transaction = transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        transactionService.recordTransaction(transaction);

        List<Transaction> result =
                transactionService.getTransactions("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactions_shouldPreserveInsertionOrder() {
        Transaction first = transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        Transaction second = transactionService.createTransaction(
                "SAV001", TransactionType.WITHDRAWAL,
                new BigDecimal("200.00"));

        transactionService.recordTransaction(first);
        transactionService.recordTransaction(second);

        List<Transaction> result =
                transactionService.getTransactions("SAV001");

        assertEquals(first, result.get(0));
        assertEquals(second, result.get(1));
    }

    @Test
    void getTransactions_shouldNotAllowModifyingStoredHistory() {
        Transaction transaction = transactionService.createTransaction(
                "SAV001", TransactionType.DEPOSIT,
                new BigDecimal("1000.00"));

        transactionService.recordTransaction(transaction);

        List<Transaction> result =
                transactionService.getTransactions("SAV001");

        assertThrows(UnsupportedOperationException.class,
                () -> result.add(transaction));
    }
}
