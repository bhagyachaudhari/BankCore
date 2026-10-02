package com.bankcore.account;

import com.bankcore.exception.InsufficientFundsException;
import com.bankcore.exception.InvalidAmountException;
import com.bankcore.exception.MinimumBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CurrentAccountTest {

    private CurrentAccount account;

    @BeforeEach
    void setUp() {
        account = new CurrentAccount(
                "CUR1001",
                "CUST1001",
                new BigDecimal("10000.00")
        );
    }

    // ---------------------------------------------------------
    // Constructor / Account Details
    // ---------------------------------------------------------

    @Test
    void shouldCreateCurrentAccountSuccessfully() {
        assertNotNull(account);

        assertEquals("CUR1001", account.getAccountNumber());
        assertEquals("CUST1001", account.getCustomerId());

        assertEquals(
                0,
                new BigDecimal("10000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldReturnCurrentAccountType() {
        assertEquals(
                AccountType.CURRENT,
                account.getAccountType()
        );
    }

    // ---------------------------------------------------------
    // Successful Withdrawals
    // ---------------------------------------------------------

    @Test
    void shouldWithdrawSuccessfully() {

        account.withdraw(new BigDecimal("2000.00"));

        assertEquals(
                0,
                new BigDecimal("8000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldAllowWithdrawalIntoOverdraft() {

        account.withdraw(new BigDecimal("12000.00"));

        assertEquals(
                0,
                new BigDecimal("-2000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldAllowWithdrawalUpToExactOverdraftLimit() {

        // Initial balance = 10,000
        // Withdrawal = 15,000
        // Remaining balance = -5,000

        account.withdraw(new BigDecimal("15000.00"));

        assertEquals(
                0,
                new BigDecimal("-5000.00")
                        .compareTo(account.getBalance())
        );
    }

    // ---------------------------------------------------------
    // Overdraft Validation
    // ---------------------------------------------------------

    @Test
    void shouldRejectWithdrawalBeyondOverdraftLimit() {

        // Initial balance = 10,000
        // Withdrawal = 15,000 -> -5,000 allowed
        // Withdrawal = 15,000.01 -> -5,000.01 rejected

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(
                        new BigDecimal("15000.01")
                )
        );

        assertEquals(
                "Overdraft limit exceeded. Current account "
                        + "cannot go below -₹5,000.",
                exception.getMessage()
        );
    }

    @Test
    void shouldNotChangeBalanceWhenOverdraftLimitIsExceeded() {

        BigDecimal initialBalance = account.getBalance();

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(
                        new BigDecimal("15000.01")
                )
        );

        assertEquals(
                0,
                initialBalance.compareTo(account.getBalance())
        );
    }

    // ---------------------------------------------------------
    // Invalid Withdrawal Amounts
    // ---------------------------------------------------------

    @Test
    void shouldRejectZeroWithdrawal() {

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(BigDecimal.ZERO)
        );

        assertEquals(
                "Withdrawal amount must be greater than zero",
                exception.getMessage()
        );

        assertEquals(
                0,
                new BigDecimal("10000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldRejectNegativeWithdrawal() {

        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(
                        new BigDecimal("-100.00")
                )
        );

        assertEquals(
                "Withdrawal amount must be greater than zero",
                exception.getMessage()
        );

        assertEquals(
                0,
                new BigDecimal("10000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldRejectNullWithdrawal() {

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> account.withdraw(null)
        );

        assertEquals(
                "Withdrawal amount cannot be null",
                exception.getMessage()
        );

        assertEquals(
                0,
                new BigDecimal("10000.00")
                        .compareTo(account.getBalance())
        );
    }

    // ---------------------------------------------------------
    // Multiple Withdrawals
    // ---------------------------------------------------------

    @Test
    void shouldAllowMultipleWithdrawalsWithinOverdraftLimit() {

        account.withdraw(new BigDecimal("5000.00"));
        // Balance = 5,000

        account.withdraw(new BigDecimal("7000.00"));
        // Balance = -2,000

        account.withdraw(new BigDecimal("3000.00"));
        // Balance = -5,000

        assertEquals(
                0,
                new BigDecimal("-5000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldRejectWithdrawalWhenAlreadyAtOverdraftLimit() {

        account.withdraw(new BigDecimal("15000.00"));

        // Balance = -5,000

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(
                        new BigDecimal("0.01")
                )
        );

        assertEquals(
                0,
                new BigDecimal("-5000.00")
                        .compareTo(account.getBalance())
        );
    }

    // ---------------------------------------------------------
    // Deposit After Overdraft
    // ---------------------------------------------------------

    @Test
    void shouldAllowDepositAfterOverdraft() {

        account.withdraw(new BigDecimal("12000.00"));
        // Balance = -2,000

        account.deposit(new BigDecimal("5000.00"));
        // Balance = 3,000

        assertEquals(
                0,
                new BigDecimal("3000.00")
                        .compareTo(account.getBalance())
        );
    }
}
