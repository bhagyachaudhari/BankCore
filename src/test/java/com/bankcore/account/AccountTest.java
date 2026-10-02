package com.bankcore.account;

import com.bankcore.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new SavingsAccount(
                "ACC1001",
                "CUST1001",
                new BigDecimal("5000.00")
        );
    }

    // Constructor tests

    @Test
    void shouldCreateAccountSuccessfully() {
        assertNotNull(account);
        assertEquals("ACC1001", account.getAccountNumber());
        assertEquals("CUST1001", account.getCustomerId());
        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SavingsAccount(
                        null, "CUST1001",
                        new BigDecimal("5000.00"))
        );

        assertEquals("accountNumber cannot be null",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SavingsAccount(
                        "ACC1001", null,
                        new BigDecimal("5000.00"))
        );

        assertEquals("customerId cannot be null",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenInitialBalanceIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SavingsAccount(
                        "ACC1001", "CUST1001", null)
        );

        assertEquals("initialBalance cannot be null",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SavingsAccount(
                        "", "CUST1001",
                        new BigDecimal("5000.00"))
        );

        assertEquals("accountNumber cannot be empty",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAccountNumberIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new SavingsAccount(
                        "   ", "CUST1001",
                        new BigDecimal("5000.00")));
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SavingsAccount(
                        "ACC1001", "",
                        new BigDecimal("5000.00"))
        );

        assertEquals("customerId cannot be empty",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new SavingsAccount(
                        "ACC1001", "   ",
                        new BigDecimal("5000.00")));
    }

    @Test
    void shouldThrowExceptionWhenInitialBalanceIsZero() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> new SavingsAccount(
                        "ACC1001", "CUST1001",
                        BigDecimal.ZERO)
        );

        assertEquals("initialBalance must be greater than zero",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenInitialBalanceIsNegative() {
        assertThrows(InvalidAmountException.class,
                () -> new SavingsAccount(
                        "ACC1001", "CUST1001",
                        new BigDecimal("-100.00")));
    }

    // Getter tests

    @Test
    void shouldReturnAccountNumber() {
        assertEquals("ACC1001", account.getAccountNumber());
    }

    @Test
    void shouldReturnCustomerId() {
        assertEquals("CUST1001", account.getCustomerId());
    }

    @Test
    void shouldReturnInitialBalance() {
        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));
    }

    // Deposit tests

    @Test
    void shouldDepositSuccessfully() {
        account.deposit(new BigDecimal("1000.00"));

        assertEquals(0, new BigDecimal("6000.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldDepositDecimalAmountSuccessfully() {
        account.deposit(new BigDecimal("250.75"));

        assertEquals(0, new BigDecimal("5250.75")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenDepositAmountIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> account.deposit(null)
        );

        assertEquals("Deposit amount cannot be null",
                exception.getMessage());

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenDepositAmountIsZero() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(BigDecimal.ZERO)
        );

        assertEquals("Deposit amount must be greater than zero",
                exception.getMessage());

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenDepositAmountIsNegative() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(new BigDecimal("-500.00"))
        );

        assertEquals("Deposit amount must be greater than zero",
                exception.getMessage());

        assertEquals(0, new BigDecimal("5000.00")
                .compareTo(account.getBalance()));
    }

    @Test
    void shouldSupportMultipleDeposits() {
        account.deposit(new BigDecimal("1000.00"));
        account.deposit(new BigDecimal("500.00"));
        account.deposit(new BigDecimal("250.50"));

        assertEquals(0, new BigDecimal("6750.50")
                .compareTo(account.getBalance()));
    }

    // Abstract method implementation tests

    @Test
    void shouldReturnSavingsAccountType() {
        assertEquals(AccountType.SAVINGS,
                account.getAccountType());
    }

    @Test
    void shouldWithdrawSuccessfullyThroughAccountReference() {
        account.withdraw(new BigDecimal("1000.00"));

        assertEquals(0, new BigDecimal("4000.00")
                .compareTo(account.getBalance()));
    }
}
