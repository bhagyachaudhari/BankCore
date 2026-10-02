package com.bankcore.service;

import com.bankcore.customer.Customer;
import com.bankcore.exception.CustomerNotFoundException;
import com.bankcore.exception.DuplicateCustomerException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceImplTest {

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl();
    }

    @Test
    void createCustomer_shouldCreateCustomer() {
        Customer customer = new Customer(
                "CUST001", "Bhagyashri", "bhagya@example.com");

        Customer result = customerService.createCustomer(customer);

        assertNotNull(result);
        assertEquals("CUST001", result.getCustomerId());
        assertEquals("Bhagyashri", result.getName());
        assertEquals("bhagya@example.com", result.getEmail());
    }

    @Test
    void createCustomer_shouldThrowExceptionForNullCustomer() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.createCustomer(null));
    }

    @Test
    void createCustomer_shouldThrowExceptionForDuplicateId() {
        Customer customer = new Customer(
                "CUST001", "Bhagyashri", "bhagya@example.com");

        customerService.createCustomer(customer);

        Customer duplicate = new Customer(
                "CUST001", "Another Name", "another@example.com");

        assertThrows(DuplicateCustomerException.class,
                () -> customerService.createCustomer(duplicate));
    }

    @Test
    void getCustomer_shouldReturnExistingCustomer() {
        Customer customer = new Customer(
                "CUST001", "Bhagyashri", "bhagya@example.com");

        customerService.createCustomer(customer);

        Customer result = customerService.getCustomer("CUST001");

        assertSame(customer, result);
    }

    @Test
    void getCustomer_shouldThrowExceptionWhenNotFound() {
        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomer("UNKNOWN"));
    }

    @Test
    void getCustomer_shouldThrowExceptionForBlankId() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.getCustomer(" "));
    }

    @Test
    void updateCustomer_shouldUpdateNameAndEmail() {
        Customer customer = new Customer(
                "CUST001", "Bhagyashri", "old@example.com");

        customerService.createCustomer(customer);

        Customer updated = customerService.updateCustomer(
                "CUST001", "Bhagyashri C", "new@example.com");

        assertEquals("CUST001", updated.getCustomerId());
        assertEquals("Bhagyashri C", updated.getName());
        assertEquals("new@example.com", updated.getEmail());

        // Original immutable object remains unchanged.
        assertEquals("Bhagyashri", customer.getName());
        assertEquals("old@example.com", customer.getEmail());

        assertSame(updated, customerService.getCustomer("CUST001"));
    }

    @Test
    void updateCustomer_shouldThrowExceptionWhenCustomerNotFound() {
        assertThrows(CustomerNotFoundException.class,
                () -> customerService.updateCustomer(
                        "UNKNOWN", "New Name", "new@example.com"));
    }

    @Test
    void updateCustomer_shouldThrowExceptionForBlankName() {
        customerService.createCustomer(new Customer(
                "CUST001", "Bhagyashri", "old@example.com"));

        assertThrows(IllegalArgumentException.class,
                () -> customerService.updateCustomer(
                        "CUST001", " ", "new@example.com"));
    }

    @Test
    void updateCustomer_shouldThrowExceptionForNullEmail() {
        customerService.createCustomer(new Customer(
                "CUST001", "Bhagyashri", "old@example.com"));

        assertThrows(NullPointerException.class,
                () -> customerService.updateCustomer(
                        "CUST001", "Bhagyashri", null));
    }

    @Test
    void deleteCustomer_shouldRemoveCustomer() {
        customerService.createCustomer(new Customer(
                "CUST001", "Bhagyashri", "bhagya@example.com"));

        customerService.deleteCustomer("CUST001");

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomer("CUST001"));
    }

    @Test
    void deleteCustomer_shouldThrowExceptionWhenNotFound() {
        assertThrows(CustomerNotFoundException.class,
                () -> customerService.deleteCustomer("UNKNOWN"));
    }

    @Test
    void getAllCustomers_shouldReturnAllCustomers() {
        Customer first = new Customer(
                "CUST001", "Bhagyashri", "bhagya@example.com");

        Customer second = new Customer(
                "CUST002", "Asha", "asha@example.com");

        customerService.createCustomer(first);
        customerService.createCustomer(second);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(second));
    }

    @Test
    void getAllCustomers_shouldReturnEmptyListWhenNoCustomersExist() {
        List<Customer> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCustomers_shouldNotExposeInternalStorage() {
        customerService.createCustomer(new Customer(
                "CUST001", "Bhagyashri", "bhagya@example.com"));

        List<Customer> result = customerService.getAllCustomers();
        result.clear();

        assertEquals(1, customerService.getAllCustomers().size());
    }
}
