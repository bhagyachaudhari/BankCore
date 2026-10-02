package com.bankcore.account;

import com.bankcore.exception.InsufficientFundsException;
import com.bankcore.exception.InvalidAmountException;
import com.bankcore.exception.MinimumBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    private SavingsAccount account;

    @BeforeEach
    void setUp() {
        account = new SavingsAccount(
                "SAV1001",
                "CUST1001",
                new BigDecimal("10000.00")
        );
    }

    // ---------------------------------------------------------
    // Constructor / Account Details
    // ---------------------------------------------------------

    @Test
    void shouldCreateSavingsAccountSuccessfully() {

        assertNotNull(account);

        assertEquals(
                "SAV1001",
                account.getAccountNumber()
        );

        assertEquals(
                "CUST1001",
                account.getCustomerId()
        );

        assertEquals(
                0,
                new BigDecimal("10000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldReturnSavingsAccountType() {

        assertEquals(
                AccountType.SAVINGS,
                account.getAccountType()
        );
    }

    // ---------------------------------------------------------
    // Minimum Balance - Constructor
    // ---------------------------------------------------------

    @Test
    void shouldAllowInitialBalanceExactlyEqualToMinimumBalance() {

        SavingsAccount savingsAccount = new SavingsAccount(
                "SAV1002",
                "CUST1002",
                new BigDecimal("1000.00")
        );

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(savingsAccount.getBalance())
        );
    }

    @Test
    void shouldAllowInitialBalanceGreaterThanMinimumBalance() {

        SavingsAccount savingsAccount = new SavingsAccount(
                "SAV1003",
                "CUST1003",
                new BigDecimal("5000.00")
        );

        assertEquals(
                0,
                new BigDecimal("5000.00")
                        .compareTo(savingsAccount.getBalance())
        );
    }

    @Test
    void shouldRejectInitialBalanceBelowMinimumBalance() {

        MinimumBalanceException exception = assertThrows(
                MinimumBalanceException.class,
                () -> new SavingsAccount(
                        "SAV1004",
                        "CUST1004",
                        new BigDecimal("999.99")
                )
        );

        assertEquals(
                "Savings account must maintain a minimum balance of ₹1,000.",
                exception.getMessage()
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
    void shouldAllowWithdrawalUpToMinimumBalance() {

        // Initial balance = ₹10,000
        // Withdrawal = ₹9,000
        // Remaining balance = ₹1,000

        account.withdraw(new BigDecimal("9000.00"));

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldAllowWithdrawalWhenRemainingBalanceIsGreaterThanMinimum() {

        // Initial balance = ₹10,000
        // Withdrawal = ₹8,000
        // Remaining balance = ₹2,000

        account.withdraw(new BigDecimal("8000.00"));

        assertEquals(
                0,
                new BigDecimal("2000.00")
                        .compareTo(account.getBalance())
        );
    }

    // ---------------------------------------------------------
    // Minimum Balance Validation
    // ---------------------------------------------------------

    @Test
    void shouldRejectWithdrawalBelowMinimumBalance() {

        // Initial balance = ₹10,000
        // Withdrawal = ₹9,000.01
        // Remaining balance = ₹999.99

        MinimumBalanceException exception = assertThrows(
                MinimumBalanceException.class,
                () -> account.withdraw(
                        new BigDecimal("9000.01")
                )
        );

        assertEquals(
                "Insufficient balance. Savings account "
                        + "must maintain a minimum balance of ₹1,000.",
                exception.getMessage()
        );
    }

    @Test
    void shouldNotChangeBalanceWhenWithdrawalIsRejected() {

        assertThrows(
                MinimumBalanceException.class,
                () -> account.withdraw(
                        new BigDecimal("9000.01")
                )
        );

        assertEquals(
                0,
                new BigDecimal("10000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldRejectWithdrawalEqualToEntireBalance() {

        // Remaining balance would be ₹0,
        // which is below the required ₹1,000.

        assertThrows(
                MinimumBalanceException.class,
                () -> account.withdraw(
                        new BigDecimal("10000.00")
                )
        );

        assertEquals(
                0,
                new BigDecimal("10000.00")
                        .compareTo(account.getBalance())
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
    void shouldAllowMultipleWithdrawalsWhileMaintainingMinimumBalance() {

        account.withdraw(new BigDecimal("3000.00"));
        // Balance = ₹7,000

        account.withdraw(new BigDecimal("2000.00"));
        // Balance = ₹5,000

        account.withdraw(new BigDecimal("4000.00"));
        // Balance = ₹1,000

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldRejectWithdrawalAfterReachingMinimumBalance() {

        account.withdraw(new BigDecimal("9000.00"));
        // Balance = ₹1,000

        assertThrows(
                MinimumBalanceException.class,
                () -> account.withdraw(
                        new BigDecimal("0.01")
                )
        );

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(account.getBalance())
        );
    }

    // ---------------------------------------------------------
    // Deposit After Withdrawal
    // ---------------------------------------------------------

    @Test
    void shouldAllowDepositAfterWithdrawal() {

        account.withdraw(new BigDecimal("9000.00"));
        // Balance = ₹1,000

        account.deposit(new BigDecimal("2000.00"));
        // Balance = ₹3,000

        assertEquals(
                0,
                new BigDecimal("3000.00")
                        .compareTo(account.getBalance())
        );
    }

    @Test
    void shouldAllowWithdrawalAgainAfterDeposit() {

        account.withdraw(new BigDecimal("9000.00"));
        // Balance = ₹1,000

        account.deposit(new BigDecimal("5000.00"));
        // Balance = ₹6,000

        account.withdraw(new BigDecimal("2000.00"));
        // Balance = ₹4,000

        assertEquals(
                0,
                new BigDecimal("4000.00")
                        .compareTo(account.getBalance())
        );
    }
}
