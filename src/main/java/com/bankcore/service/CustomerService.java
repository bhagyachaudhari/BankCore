package com.bankcore.service;

import com.bankcore.customer.Customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    Customer getCustomer(String customerId);

    Customer updateCustomer(String customerId,
                            String name,
                            String email);

    void deleteCustomer(String customerId);

    List<Customer> getAllCustomers();
}
