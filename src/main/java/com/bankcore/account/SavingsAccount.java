package com.bankcore.account;

import com.bankcore.exception.MinimumBalanceException;

import java.math.BigDecimal;

public class SavingsAccount extends Account {

    private static final BigDecimal MINIMUM_BALANCE =
            new BigDecimal("1000.00");

    public SavingsAccount(String accountNumber,
                          String customerId,
                          BigDecimal initialBalance) {

        super(accountNumber, customerId, initialBalance);

        if (initialBalance.compareTo(MINIMUM_BALANCE) < 0) {
            throw new MinimumBalanceException(
                    "Savings account must maintain a minimum "
                            + "balance of ₹1,000.");
        }
    }

    @Override
    public void withdraw(BigDecimal amount) {

        validatePositiveAmount(amount, "Withdrawal");

        BigDecimal remainingBalance =
                getBalance().subtract(amount);

        if (remainingBalance.compareTo(MINIMUM_BALANCE) < 0) {
            throw new MinimumBalanceException(
                    "Insufficient balance. Savings account "
                            + "must maintain a minimum balance of ₹1,000.");
        }

        subtractFromBalance(amount);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }

}
