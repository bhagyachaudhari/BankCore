package com.bankcore.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private Account account;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        account = mock(Account.class);
        accountService = new AccountService(account);
    }

    @Test
    void shouldCreateAccountServiceSuccessfully() {

        assertNotNull(accountService);
    }

    @Test
    void shouldThrowExceptionWhenAccountIsNull() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccountService(null)
        );

        assertEquals(
                "account should not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldDelegateDepositToAccount() {

        BigDecimal amount = new BigDecimal("500.00");

        accountService.deposit(amount);

        verify(account).deposit(amount);
    }

    @Test
    void shouldDelegateWithdrawToAccount() {

        BigDecimal amount = new BigDecimal("300.00");

        accountService.withdraw(amount);

        verify(account).withdraw(amount);
    }

    @Test
    void shouldDelegateZeroDepositToAccount() {

        BigDecimal amount = BigDecimal.ZERO;

        accountService.deposit(amount);

        verify(account).deposit(amount);
    }

    @Test
    void shouldDelegateNegativeDepositToAccount() {

        BigDecimal amount = new BigDecimal("-100.00");

        accountService.deposit(amount);

        verify(account).deposit(amount);
    }

    @Test
    void shouldDelegateNullDepositToAccount() {

        accountService.deposit(null);

        verify(account).deposit(null);
    }

    @Test
    void shouldDelegateZeroWithdrawalToAccount() {

        BigDecimal amount = BigDecimal.ZERO;

        accountService.withdraw(amount);

        verify(account).withdraw(amount);
    }

    @Test
    void shouldDelegateNegativeWithdrawalToAccount() {

        BigDecimal amount = new BigDecimal("-100.00");

        accountService.withdraw(amount);

        verify(account).withdraw(amount);
    }

    @Test
    void shouldDelegateNullWithdrawalToAccount() {

        accountService.withdraw(null);

        verify(account).withdraw(null);
    }
}