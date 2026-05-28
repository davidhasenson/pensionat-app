package org.example.pensionatapp.pensionat.customer.service;

import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.dto.CreateCustomerRequest;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.model.dto.CustomerResponse;
import org.example.pensionatapp.pensionat.customer.model.dto.UpdateCustomerRequest;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
                "070-111"
        );
        customer1.setId(1L);

        customer2 = new Customer(
                "Samweis",
                "Gamdji",
                "sam@pensionat.se",
                "070-222"
        );
        customer2.setId(2L);

        fakeCustomers = List.of(customer1, customer2);
    }

    @Test
    void createCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Frodo",
                "Bagger",
                "frodo@pensionat.se",
                "070-111"
        );

        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);

        CustomerResponse result = customerService.createCustomer(request);

        assertNotNull(result, "Den skapade kunden borde inte vara null");
        assertEquals(1L, result.id(), "Kunden borde ha fått ID 1L från vår mock");
        assertEquals("Frodo", result.firstName(), "Förnamnet matchar inte");
        assertEquals("Bagger", result.lastName(), "Efternamnet matchar inte");
        assertEquals("frodo@pensionat.se", result.email(), "E-posten matchar inte");
        assertEquals("070-111", result.phone(), "Telefonnumret matchar inte");

        verify(customerRepository, org.mockito.Mockito.times(1)).save(any(Customer.class));

    }

    @Test
    void updateCustomer() {
        Long customerId = 1L;
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Bilbo",
                "Bagger",
                "070-999"
        );

        when(customerRepository.findById(customerId))
                .thenReturn(java.util.Optional.of(customer1));

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse result = customerService.updateCustomerById(customerId, request);

        assertNotNull(result, "Det uppdaterade resultatet borde inte vara null");
        assertEquals("Bilbo", result.firstName(), "Förnamnet uppdaterades inte");
        assertEquals("Bagger", result.lastName(), "Efternamnet uppdaterades inte");
        assertEquals("070-999", result.phone(), "Telefonnumret uppdaterades inte");
        assertEquals("frodo@pensionat.se", result.email(), "E-posten ska inte ha ändrats");

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_ShouldThrowNotFoundException_WhenCustomerDoesNotExist() {
        Long invalidId = 99L;
        UpdateCustomerRequest request = new UpdateCustomerRequest("Bilbo", "Bagger", "070-999");

        when(customerRepository.findById(invalidId))
                .thenReturn(empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> customerService.updateCustomerById(invalidId, request)
        );

        assertEquals("Kunden hittades inte", exception.getMessage());

        verify(customerRepository, never()).save(any(Customer.class));
    }
}