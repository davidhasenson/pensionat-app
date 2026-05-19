package org.example.pensionatapp.pensionat.customer.service;

import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.CreateCustomerRequest;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer1;
    private Customer customer2;

    List<Customer> fakeCustomers;

    @BeforeEach
    void setUp() {

        customer1 = new Customer(
                "Frodo",
                "Bagger",
                "frodo@pensionat.se",
                "070-111",
                "ring123"
        );
        customer1.setId(1L);

        customer2 = new Customer(
                "Samweis",
                "Gamdji",
                "sam@pensionat.se",
                "070-222",
                "rep123"
        );
        customer2.setId(2L);

        fakeCustomers = List.of(customer1, customer2);
    }

    @Test
    void getAllCustomers() {
    }

    @Test
    void getCustomerById() {
    }

    @Test
    void createCustomer() {

        // Arrange

        CreateCustomerRequest request = new CreateCustomerRequest(
                "Frodo",
                "Bagger",
                "frodo@pensionat.se",
                "070-111",
                "ring123"
        );

        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);

        // Act

        Customer result = customerService.createCustomer(request);

        // Assert

        assertNotNull(result, "Den skapade kunden borde inte vara null");
        assertEquals(1L, result.getId(), "Kunden borde ha fått ID 1L från vår mock");
        assertEquals("Frodo", result.getFirstName(), "Förnamnet matchar inte");
        assertEquals("Bagger", result.getLastName(), "Efternamnet matchar inte");
        assertEquals("frodo@pensionat.se", result.getEmail(), "E-posten matchar inte");

        verify(customerRepository, org.mockito.Mockito.times(1)).save(any(Customer.class));

    }

    @Test
    void updateCustomer() {
    }

    @Test
    void deleteCustomer() {
    }
}