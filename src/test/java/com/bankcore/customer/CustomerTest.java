package com.bankcore.customer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomerSuccessfully() {

        Customer customer = new Customer(
                "CUST1001",
                "John Doe",
                "john.doe@example.com"
        );

        assertNotNull(customer);
        assertEquals("CUST1001", customer.getCustomerId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john.doe@example.com", customer.getEmail());
    }

    @Test
    void shouldReturnCustomerId() {

        Customer customer = new Customer(
                "CUST1001",
                "John Doe",
                "john.doe@example.com"
        );

        assertEquals("CUST1001", customer.getCustomerId());
    }

    @Test
    void shouldReturnName() {

        Customer customer = new Customer(
                "CUST1001",
                "John Doe",
                "john.doe@example.com"
        );

        assertEquals("John Doe", customer.getName());
    }

    @Test
    void shouldReturnEmail() {

        Customer customer = new Customer(
                "CUST1001",
                "John Doe",
                "john.doe@example.com"
        );

        assertEquals("john.doe@example.com", customer.getEmail());
    }

    @Test
    void shouldReturnCorrectToString() {

        Customer customer = new Customer(
                "CUST1001",
                "John Doe",
                "john.doe@example.com"
        );

        String expected = "Customer{" +
                "customerId='CUST1001', " +
                "name='John Doe', " +
                "email='john.doe@example.com'" +
                "}";

        assertEquals(expected, customer.toString());
    }

}