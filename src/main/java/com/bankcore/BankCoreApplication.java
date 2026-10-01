package com.bankcore;

import com.bankcore.account.Account;
import com.bankcore.account.AccountService;
import com.bankcore.account.AccountType;
import com.bankcore.customer.Customer;

import java.math.BigDecimal;

public class BankCoreApplication {

    public static void main(String[] args) {

        Customer customer = new Customer("C001", "Rahul", "rahul@example.com");

        Account account = new Account("ACC10001", "C001", AccountType.SAVINGS,
                new BigDecimal("10000.00"));

        System.out.println("Customer: " + customer.getName());
        System.out.println("Account: " + account.getAccountNumber());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Initial Balance: " + account.getBalance());

        AccountService accountService = new AccountService(account);

        accountService.deposit(new BigDecimal("5000.00"));
        System.out.println("After Deposit: " + account.getBalance());

        accountService.withdraw(new BigDecimal("2000.00"));
        System.out.println("After Withdrawal: " + account.getBalance());

        try {
            accountService.withdraw(new BigDecimal("20000.00"));
        } catch (IllegalArgumentException e) {
            System.out.println("Withdrawal rejected: " + e.getMessage());
        }

        System.out.println("Final Balance: " + account.getBalance());

    }
}