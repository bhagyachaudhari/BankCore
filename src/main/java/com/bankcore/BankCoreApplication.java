package com.bankcore;

import com.bankcore.account.*;
import com.bankcore.customer.Customer;
import com.bankcore.service.AccountService;

import java.math.BigDecimal;

public class BankCoreApplication {

    public static void main(String[] args) {

        Customer customer1 = new Customer("C001", "Bhagyashri", "bhagya@example.com");
        Customer customer2 = new Customer("C002", "Sonali", "sona@example.com");

        Account savings = new SavingsAccount(
                "SAV001", "C001", new BigDecimal("10000.00"));

        Account current = new CurrentAccount(
                "CUR001", "C002", new BigDecimal("10000.00"));

        //AccountService savingsService = new AccountService(savings);
        //AccountService currentService = new AccountService(current);

        //savingsService.withdraw(null);
        //currentService.withdraw(null);

        System.out.println(savings.getAccountType()
                + " Balance: " + savings.getBalance());

        System.out.println(current.getAccountType()
                + " Balance: " + current.getBalance());
    }
}