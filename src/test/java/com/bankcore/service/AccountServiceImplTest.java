package com.bankcore.service;

import com.bankcore.account.Account;
import com.bankcore.account.AccountType;
import com.bankcore.customer.Customer;
import com.bankcore.exception.AccountAlreadyExistsException;
import com.bankcore.exception.AccountNotFoundException;
import com.bankcore.exception.CustomerNotFoundException;
import com.bankcore.exception.InvalidAmountException;
import com.bankcore.transaction.Transaction;
import com.bankcore.transaction.TransactionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplDay6Test {

    private TransactionService transactionService;
    private CustomerService customerService;
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        customerService = mock(CustomerService.class);

        accountService = new AccountServiceImpl(
                transactionService, customerService);

        when(customerService.getCustomer("CUST001"))
                .thenReturn(new Customer(
                        "CUST001", "Bhagyashri", "bhagya@example.com"));

        when(customerService.getCustomer("CUST002"))
                .thenReturn(new Customer(
                        "CUST002", "Asha", "asha@example.com"));
    }

    // -------- SAVINGS ACCOUNT TESTS --------

    @Test
    void createSavingsAccount_shouldCreateAccountForExistingCustomer() {
        Account account = accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        assertNotNull(account);
        assertEquals("SAV001", account.getAccountNumber());
        assertEquals("CUST001", account.getCustomerId());
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));

        verify(customerService).getCustomer("CUST001");
    }

    @Test
    void createSavingsAccount_shouldRejectNonexistentCustomer() {
        when(customerService.getCustomer("UNKNOWN"))
                .thenThrow(new CustomerNotFoundException(
                        "Customer not found: UNKNOWN"));

        assertThrows(CustomerNotFoundException.class,
                () -> accountService.createSavingsAccount(
                        "SAV001", "UNKNOWN",
                        new BigDecimal("5000.00")));

        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccount("SAV001"));

        verify(customerService).getCustomer("UNKNOWN");
    }

    @Test
    void createSavingsAccount_shouldRejectDuplicateAccountNumber() {
        accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        assertThrows(AccountAlreadyExistsException.class,
                () -> accountService.createSavingsAccount(
                        "SAV001", "CUST002",
                        new BigDecimal("7000.00")));

        // Existing account remains unchanged.
        Account existing = accountService.getAccount("SAV001");
        assertEquals("CUST001", existing.getCustomerId());
        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(existing.getBalance()));

        // Duplicate check occurs before customer lookup.
        verify(customerService, times(1)).getCustomer("CUST001");
        verify(customerService, never()).getCustomer("CUST002");
    }

    // -------- CURRENT ACCOUNT TESTS --------

    @Test
    void createCurrentAccount_shouldCreateAccountForExistingCustomer() {
        Account account = accountService.createCurrentAccount(
                "CUR001", "CUST002", new BigDecimal("8000.00"));

        assertNotNull(account);
        assertEquals("CUR001", account.getAccountNumber());
        assertEquals("CUST002", account.getCustomerId());
        assertEquals(AccountType.CURRENT, account.getAccountType());
    }

    @Test
    void createCurrentAccount_shouldRejectNonexistentCustomer() {
        when(customerService.getCustomer("UNKNOWN"))
                .thenThrow(new CustomerNotFoundException(
                        "Customer not found: UNKNOWN"));

        assertThrows(CustomerNotFoundException.class,
                () -> accountService.createCurrentAccount(
                        "CUR001", "UNKNOWN",
                        new BigDecimal("8000.00")));

        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccount("CUR001"));
    }

    @Test
    void createCurrentAccount_shouldRejectDuplicateAccountNumber() {
        accountService.createCurrentAccount(
                "CUR001", "CUST001", new BigDecimal("8000.00"));

        assertThrows(AccountAlreadyExistsException.class,
                () -> accountService.createCurrentAccount(
                        "CUR001", "CUST002",
                        new BigDecimal("9000.00")));
    }

    // -------- INPUT VALIDATION TESTS --------

    @Test
    void createAccount_shouldRejectNullAccountNumber() {
        assertThrows(NullPointerException.class,
                () -> accountService.createSavingsAccount(
                        null, "CUST001",
                        new BigDecimal("5000.00")));

        verifyNoInteractions(customerService);
    }

    @Test
    void createAccount_shouldRejectBlankAccountNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> accountService.createSavingsAccount(
                        " ", "CUST001",
                        new BigDecimal("5000.00")));

        verifyNoInteractions(customerService);
    }

    @Test
    void createAccount_shouldRejectNullCustomerId() {
        assertThrows(NullPointerException.class,
                () -> accountService.createSavingsAccount(
                        "SAV001", null,
                        new BigDecimal("5000.00")));

        verifyNoInteractions(customerService);
    }

    @Test
    void createAccount_shouldRejectInvalidInitialBalance() {
        assertThrows(InvalidAmountException.class,
                () -> accountService.createSavingsAccount(
                        "SAV001", "CUST001", BigDecimal.ZERO));

        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccount("SAV001"));
    }

    // -------- ACCOUNT-CUSTOMER ASSOCIATION TESTS --------

    @Test
    void getAccount_shouldPreserveCustomerAssociation() {
        accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        Account account = accountService.getAccount("SAV001");

        assertEquals("CUST001", account.getCustomerId());
    }

    // -------- EXISTING OPERATIONS REGRESSION TESTS --------

    @Test
    void deposit_shouldStillWorkWithCustomerAssociation() {
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

        verify(transactionService).recordTransaction(transaction);
    }

    @Test
    void getTransactions_shouldReturnHistoryForExistingAccount() {
        accountService.createSavingsAccount(
                "SAV001", "CUST001", new BigDecimal("5000.00"));

        List<Transaction> expected = List.of(mock(Transaction.class));

        when(transactionService.getTransactions("SAV001"))
                .thenReturn(expected);

        List<Transaction> actual =
                accountService.getTransactions("SAV001");

        assertSame(expected, actual);
        verify(transactionService).getTransactions("SAV001");
    }

    @Test
    void getTransactions_shouldRejectUnknownAccount() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.getTransactions("UNKNOWN"));

        verifyNoInteractions(transactionService);
    }
}
