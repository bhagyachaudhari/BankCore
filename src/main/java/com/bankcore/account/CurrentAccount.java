package com.bankcore.account;

import com.bankcore.exception.InsufficientFundsException;

import java.math.BigDecimal;

public class CurrentAccount extends Account {

    private static final BigDecimal OVERDRAFT_LIMIT =
            new BigDecimal("5000.00");

    public CurrentAccount(String accountNumber,
                          String customerId, BigDecimal initialBalance) {

        super(accountNumber, customerId, initialBalance);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        validateActive();
        validatePositiveAmount(amount, "Withdrawal");

        BigDecimal remainingBalance = getBalance().subtract(amount);
        BigDecimal minimumAllowedBalance = OVERDRAFT_LIMIT.negate();

        if (remainingBalance.compareTo(minimumAllowedBalance) < 0) {
            throw new InsufficientFundsException(
                    "Overdraft limit exceeded. Current account "
                            + "cannot go below -₹5,000.");
        }

        subtractFromBalance(amount);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CURRENT;
    }


}
