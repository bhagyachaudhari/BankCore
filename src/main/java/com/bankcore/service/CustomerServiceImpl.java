package com.bankcore.service;

import com.bankcore.customer.Customer;
import com.bankcore.exception.CustomerNotFoundException;
import com.bankcore.exception.DuplicateCustomerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerServiceImpl implements CustomerService {

    private final Map<String, Customer> customers = new HashMap<>();

    @Override
    public Customer createCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null");
        }

        String customerId = customer.getCustomerId();

        if (customers.containsKey(customerId)) {
            throw new DuplicateCustomerException(
                    "Customer already exists: " + customerId);
        }

        customers.put(customerId, customer);
        return customer;
    }

    @Override
    public Customer getCustomer(String customerId) {
        validateCustomerId(customerId);

        Customer customer = customers.get(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException(
                    "Customer not found: " + customerId);
        }

        return customer;
    }

    @Override
    public Customer updateCustomer(String customerId,
                                   String name,
                                   String email) {
        Customer existingCustomer = getCustomer(customerId);

        // Customer is immutable, so create a replacement object.
        Customer updatedCustomer = new Customer(
                existingCustomer.getCustomerId(),
                name,
                email);

        customers.put(customerId, updatedCustomer);

        return updatedCustomer;
    }

    @Override
    public void deleteCustomer(String customerId) {
        getCustomer(customerId);
        customers.remove(customerId);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    private void validateCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be null or blank");
        }
    }
}
