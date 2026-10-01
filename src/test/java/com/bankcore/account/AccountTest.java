package com.bankcore.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account(
                "ACC1001",
                "CUST1001",
                AccountType.SAVINGS,
                new BigDecimal("1000.00")
        );
    }

    // Constructor Tests

    @Test
    void shouldCreateAccountSuccessfully() {
        assertNotNull(account);
        assertEquals("ACC1001", account.getAccountNumber());
        assertEquals("CUST1001", account.getCustomerId());
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(0, new BigDecimal("1000.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Account(
                        "ACC1001", "CUST1001",
                        AccountType.SAVINGS, null
                )
        );

        assertEquals("balance should not be null or negative",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account(
                        "ACC1001", "CUST1001",
                        AccountType.SAVINGS, BigDecimal.ZERO
                ));
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account(
                        "ACC1001", "CUST1001",
                        AccountType.SAVINGS, new BigDecimal("-100")
                ));
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account(
                        null, "CUST1001",
                        AccountType.SAVINGS, new BigDecimal("1000")
                ));
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account(
                        "", "CUST1001",
                        AccountType.SAVINGS, new BigDecimal("1000")
                ));
    }

    @Test
    void shouldThrowExceptionWhenAccountTypeIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account(
                        "ACC1001", "CUST1001",
                        null, new BigDecimal("1000")
                ));
    }

    // Deposit Tests

    @Test
    void shouldDepositAmountSuccessfully() {
        account.deposit(new BigDecimal("500.00"));

        assertEquals(0, new BigDecimal("1500.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldDepositSmallAmountSuccessfully() {
        account.deposit(new BigDecimal("0.01"));

        assertEquals(0, new BigDecimal("1000.01")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenDepositAmountIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> account.deposit(null));

        assertEquals(0, new BigDecimal("1000.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenDepositAmountIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> account.deposit(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowExceptionWhenDepositAmountIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> account.deposit(new BigDecimal("-100")));
    }

    // Withdrawal Tests

    @Test
    void shouldWithdrawAmountSuccessfully() {
        account.withdraw(new BigDecimal("300.00"));

        assertEquals(0, new BigDecimal("700.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldWithdrawFullBalanceSuccessfully() {
        account.withdraw(new BigDecimal("1000.00"));

        assertEquals(0, BigDecimal.ZERO
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenWithdrawAmountExceedsBalance() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(new BigDecimal("1500.00"))
        );

        assertEquals("Insufficient balance.", exception.getMessage());
        assertEquals(0, new BigDecimal("1000.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenWithdrawAmountIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(null));
    }

    @Test
    void shouldThrowExceptionWhenWithdrawAmountIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowExceptionWhenWithdrawAmountIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(new BigDecimal("-100")));
    }

    // Multiple Transaction Tests

    @Test
    void shouldUpdateBalanceAfterMultipleTransactions() {
        account.deposit(new BigDecimal("500.00"));
        account.withdraw(new BigDecimal("200.00"));
        account.deposit(new BigDecimal("100.00"));

        assertEquals(0, new BigDecimal("1400.00")
                .compareTo(account.getBalance()));
    }
}
