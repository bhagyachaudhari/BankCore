package com.bankcore.service;

import com.bankcore.account.Account;
import com.bankcore.account.AccountStatus;
import com.bankcore.customer.Customer;
import com.bankcore.exception.AccountFrozenException;
import com.bankcore.exception.AccountNotFoundException;
import com.bankcore.exception.InvalidAccountStateException;
import com.bankcore.transaction.Transaction;
import com.bankcore.transaction.TransactionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountLifecycleTest {

    private AccountServiceImpl accountService;
    private CustomerService customerService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        customerService = mock(CustomerService.class);
        transactionService = mock(TransactionService.class);

        when(customerService.getCustomer("CUST001"))
                .thenReturn(new Customer(
                        "CUST001", "Bhagyashri", "bhagya@example.com"));

        when(customerService.getCustomer("CUST002"))
                .thenReturn(new Customer(
                        "CUST002", "Asha", "asha@example.com"));

        accountService = new AccountServiceImpl(
                transactionService, customerService);
    }

    private Account createCurrentAccount(String accountNumber,
                                         String customerId,
                                         String balance) {
        return accountService.createCurrentAccount(
                accountNumber, customerId, new BigDecimal(balance));
    }

    // -------- INITIAL STATUS --------

    @Test
    void newAccount_shouldHaveActiveStatus() {
        Account account = createCurrentAccount(
                "CUR001", "CUST001", "5000.00");

        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertTrue(account.isActive());
    }

    // -------- FREEZE TESTS --------

    @Test
    void freezeAccount_shouldChangeStatusToFrozen() {
        Account account = createCurrentAccount(
                "CUR001", "CUST001", "5000.00");

        accountService.freezeAccount("CUR001");

        assertEquals(AccountStatus.FROZEN, account.getStatus());
        assertFalse(account.isActive());
    }

    @Test
    void freezeAccount_shouldThrowExceptionWhenAlreadyFrozen() {
        accountService.freezeAccount(
                "CUR001"); // Account doesn't exist yet
    }

    @Test
    void freezeAccount_shouldThrowExceptionForUnknownAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.freezeAccount("UNKNOWN"));
    }

    @Test
    void freezeAccount_shouldRejectRepeatedFreeze() {
        accountService.createCurrentAccount(
                "CUR001", "CUST001", new BigDecimal("5000.00"));

        accountService.freezeAccount("CUR001");

        assertThrows(InvalidAccountStateException.class,
                () -> accountService.freezeAccount("CUR001"));
    }

    // -------- REACTIVATION TESTS --------

    @Test
    void reactivateAccount_shouldChangeStatusToActive() {
        Account account = createCurrentAccount(
                "CUR001", "CUST001", "5000.00");

        accountService.freezeAccount("CUR001");
        accountService.reactivateAccount("CUR001");

        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertTrue(account.isActive());
    }

    @Test
    void reactivateAccount_shouldRejectAlreadyActiveAccount() {
        createCurrentAccount("CUR001", "CUST001", "5000.00");

        assertThrows(InvalidAccountStateException.class,
                () -> accountService.reactivateAccount("CUR001"));
    }

    @Test
    void reactivateAccount_shouldThrowExceptionForUnknownAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.reactivateAccount("UNKNOWN"));
    }

    // -------- FROZEN ACCOUNT OPERATION TESTS --------

    @Test
    void frozenAccount_shouldRejectDeposit() {
        Account account = createCurrentAccount(
                "CUR001", "CUST001", "5000.00");

        accountService.freezeAccount("CUR001");

        assertThrows(AccountFrozenException.class,
                () -> accountService.deposit(
                        "CUR001", new BigDecimal("500.00")));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));

        verifyNoInteractions(transactionService);
    }

    @Test
    void frozenAccount_shouldRejectWithdrawal() {
        Account account = createCurrentAccount(
                "CUR001", "CUST001", "5000.00");

        accountService.freezeAccount("CUR001");

        assertThrows(AccountFrozenException.class,
                () -> accountService.withdraw(
                        "CUR001", new BigDecimal("500.00")));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));

        verifyNoInteractions(transactionService);
    }

    @Test
    void frozenSourceAccount_shouldRejectTransferWithoutChangingBalances() {
        Account source = createCurrentAccount(
                "CUR001", "CUST001", "5000.00");

        Account destination = createCurrentAccount(
                "CUR002", "CUST002", "3000.00");

        accountService.freezeAccount("CUR001");

        assertThrows(AccountFrozenException.class,
                () -> accountService.transfer(
                        "CUR001", "CUR002",
                        new BigDecimal("1000.00")));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(source.getBalance()));

        assertEquals(0, new BigDecimal("3000.00")
                .compareTo(destination.getBalance()));

        verifyNoInteractions(transactionService);
    }

    @Test
    void frozenDestinationAccount_shouldRejectTransferWithoutDebitingSource() {
        Account source = createCurrentAccount(
                "CUR001", "CUST001", "5000.00");

        Account destination = createCurrentAccount(
                "CUR002", "CUST002", "3000.00");

        accountService.freezeAccount("CUR002");

        assertThrows(AccountFrozenException.class,
                () -> accountService.transfer(
                        "CUR001", "CUR002",
                        new BigDecimal("1000.00")));

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(source.getBalance()));

        assertEquals(0, new BigDecimal("3000.00")
                .compareTo(destination.getBalance()));

        verifyNoInteractions(transactionService);
    }

    // -------- REACTIVATED ACCOUNT TESTS --------

    @Test
    void reactivatedAccount_shouldAllowDeposit() {
        Account account = createCurrentAccount(
                "CUR001", "CUST001", "5000.00");

        Transaction transaction = mock(Transaction.class);

        when(transactionService.createTransaction(
                "CUR001", TransactionType.DEPOSIT,
                new BigDecimal("500.00")))
                .thenReturn(transaction);

        accountService.freezeAccount("CUR001");
        accountService.reactivateAccount("CUR001");

        accountService.deposit("CUR001", new BigDecimal("500.00"));

        assertEquals(0, new BigDecimal("5500.00")
                .compareTo(account.getBalance()));

        verify(transactionService).recordTransaction(transaction);
    }
}