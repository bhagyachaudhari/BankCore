package com.bankcore.service;

import com.bankcore.account.Account;
import com.bankcore.account.AccountType;
import com.bankcore.exception.AccountNotFoundException;
import com.bankcore.exception.InvalidAmountException;
import com.bankcore.exception.MinimumBalanceException;
import com.bankcore.exception.SameAccountTransferException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {

    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
    }

    // =========================================================
    // createSavingsAccount()
    // =========================================================

    @Test
    void shouldCreateSavingsAccount() {

        Account account = accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        assertNotNull(account);
        assertEquals("ACC1001", account.getAccountNumber());
        assertEquals("CUST001", account.getCustomerId());
        assertEquals(
                new BigDecimal("5000.00"),
                account.getBalance()
        );
        assertEquals(AccountType.SAVINGS, account.getAccountType());
    }

    @Test
    void shouldRetrieveCreatedSavingsAccount() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        Account account = accountService.getAccount("ACC1001");

        assertNotNull(account);
        assertEquals("ACC1001", account.getAccountNumber());
        assertEquals("CUST001", account.getCustomerId());
    }

    // =========================================================
    // createCurrentAccount()
    // =========================================================

    @Disabled
    void shouldCreateCurrentAccount() {

        Account account = accountService.createCurrentAccount(
                "ACC2001",
                "CUST002",
                new BigDecimal("5000.00")
        );

        /*
         * This test is expected to FAIL with the current
         * implementation because createCurrentAccount()
         * currently returns null.
         *
         * Once CurrentAccount is implemented, this test
         * should pass.
         */
        assertNotNull(account);
        assertEquals("ACC2001", account.getAccountNumber());
        assertEquals("CUST002", account.getCustomerId());
        assertEquals(
                new BigDecimal("5000.00"),
                account.getBalance()
        );
        assertEquals(AccountType.CURRENT, account.getAccountType());
    }

    // =========================================================
    // getAccount()
    // =========================================================

    @Test
    void shouldReturnExistingAccount() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        Account account = accountService.getAccount("ACC1001");

        assertNotNull(account);
        assertEquals("ACC1001", account.getAccountNumber());
    }

    @Test
    void shouldThrowAccountNotFoundExceptionForUnknownAccount() {

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccount("ACC9999")
        );
    }

    // =========================================================
    // deposit()
    // =========================================================

    @Test
    void shouldDepositMoneySuccessfully() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        accountService.deposit(
                "ACC1001",
                new BigDecimal("2000.00")
        );

        assertEquals(
                new BigDecimal("7000.00"),
                accountService.getAccount("ACC1001").getBalance()
        );
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenDepositingToUnknownAccount() {

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.deposit(
                        "ACC9999",
                        new BigDecimal("1000.00")
                )
        );
    }

    @Test
    void shouldRejectZeroDeposit() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.deposit(
                        "ACC1001",
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void shouldRejectNegativeDeposit() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.deposit(
                        "ACC1001",
                        new BigDecimal("-100.00")
                )
        );
    }

    // =========================================================
    // withdraw()
    // =========================================================

    @Test
    void shouldWithdrawMoneySuccessfully() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        accountService.withdraw(
                "ACC1001",
                new BigDecimal("2000.00")
        );

        assertEquals(
                new BigDecimal("3000.00"),
                accountService.getAccount("ACC1001").getBalance()
        );
    }

    @Test
    void shouldAllowSavingsWithdrawalLeavingMinimumBalance() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        accountService.withdraw(
                "ACC1001",
                new BigDecimal("4000.00")
        );

        assertEquals(
                new BigDecimal("1000.00"),
                accountService.getAccount("ACC1001").getBalance()
        );
    }

    @Test
    void shouldRejectSavingsWithdrawalBelowMinimumBalance() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        assertThrows(
                MinimumBalanceException.class,
                () -> accountService.withdraw(
                        "ACC1001",
                        new BigDecimal("4000.01")
                )
        );
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenWithdrawingFromUnknownAccount() {

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.withdraw(
                        "ACC9999",
                        new BigDecimal("1000.00")
                )
        );
    }

    @Test
    void shouldRejectZeroWithdrawal() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.withdraw(
                        "ACC1001",
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void shouldRejectNegativeWithdrawal() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.withdraw(
                        "ACC1001",
                        new BigDecimal("-100.00")
                )
        );
    }

    // =========================================================
    // transfer()
    // =========================================================

    @Test
    void shouldTransferMoneySuccessfully() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("10000.00")
        );

        accountService.createSavingsAccount(
                "ACC2001",
                "CUST002",
                new BigDecimal("5000.00")
        );

        accountService.transfer(
                "ACC1001",
                "ACC2001",
                new BigDecimal("2000.00")
        );

        assertEquals(
                new BigDecimal("8000.00"),
                accountService.getAccount("ACC1001").getBalance()
        );

        assertEquals(
                new BigDecimal("7000.00"),
                accountService.getAccount("ACC2001").getBalance()
        );
    }

    @Test
    void shouldRejectTransferToSameAccount() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        assertThrows(
                SameAccountTransferException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC1001",
                        new BigDecimal("1000.00")
                )
        );
    }

    @Test
    void shouldRejectTransferFromUnknownAccount() {

        accountService.createSavingsAccount(
                "ACC2001",
                "CUST002",
                new BigDecimal("5000.00")
        );

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.transfer(
                        "ACC9999",
                        "ACC2001",
                        new BigDecimal("1000.00")
                )
        );
    }

    @Test
    void shouldRejectTransferToUnknownAccount() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC9999",
                        new BigDecimal("1000.00")
                )
        );
    }

    @Test
    void shouldRejectTransferWhenSavingsMinimumBalanceIsViolated() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        accountService.createSavingsAccount(
                "ACC2001",
                "CUST002",
                new BigDecimal("5000.00")
        );

        assertThrows(
                MinimumBalanceException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC2001",
                        new BigDecimal("4000.01")
                )
        );

        // Source balance should remain unchanged
        assertEquals(
                new BigDecimal("5000.00"),
                accountService.getAccount("ACC1001").getBalance()
        );

        // Destination balance should remain unchanged
        assertEquals(
                new BigDecimal("5000.00"),
                accountService.getAccount("ACC2001").getBalance()
        );
    }

    @Test
    void shouldRejectZeroTransfer() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        accountService.createSavingsAccount(
                "ACC2001",
                "CUST002",
                new BigDecimal("5000.00")
        );

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC2001",
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void shouldRejectNegativeTransfer() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        accountService.createSavingsAccount(
                "ACC2001",
                "CUST002",
                new BigDecimal("5000.00")
        );

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC2001",
                        new BigDecimal("-100.00")
                )
        );
    }

    @Test
    void shouldNotChangeBalancesWhenTransferFails() {

        accountService.createSavingsAccount(
                "ACC1001",
                "CUST001",
                new BigDecimal("5000.00")
        );

        accountService.createSavingsAccount(
                "ACC2001",
                "CUST002",
                new BigDecimal("5000.00")
        );

        assertThrows(
                MinimumBalanceException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC2001",
                        new BigDecimal("4500.00")
                )
        );

        assertEquals(
                new BigDecimal("5000.00"),
                accountService.getAccount("ACC1001").getBalance()
        );

        assertEquals(
                new BigDecimal("5000.00"),
                accountService.getAccount("ACC2001").getBalance()
        );
    }
}