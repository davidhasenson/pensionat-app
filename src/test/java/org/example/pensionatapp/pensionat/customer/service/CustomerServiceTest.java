package org.example.pensionatapp.pensionat.customer.service;

import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.CreateCustomerRequest;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.model.CustomerResponse;
import org.example.pensionatapp.pensionat.customer.model.UpdateCustomerRequest;
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
                "070-111"
        );

        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);

        // Act

        CustomerResponse result = customerService.createCustomer(request);

        // Assert

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

        // 1. Arrange

        Long customerId = 1L;
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Bilbo",
                "Bagger",
                "070-999"
        );

        // Säg till databasen att returnera din befintliga customer1 när vi söker på ID 1
        when(customerRepository.findById(customerId))
                .thenReturn(java.util.Optional.of(customer1));

        // Säg till databasen att returnera kunden när den sparas
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act

        CustomerResponse result = customerService.updateCustomerById(customerId, request);

        // Assert
        //              1. Förväntat    2. Faktiskt resultat    3. Felmeddelande
        assertNotNull(result, "Det uppdaterade resultatet borde inte vara null");
        assertEquals("Bilbo", result.firstName(), "Förnamnet uppdaterades inte");
        assertEquals("Bagger", result.lastName(), "Efternamnet uppdaterades inte");
        assertEquals("070-999", result.phone(), "Telefonnumret uppdaterades inte");

        // E-posten skickades inte med i din UpdateRequest, så den ska vara kvar som förut
        assertEquals("frodo@pensionat.se", result.email(), "E-posten ska inte ha ändrats");

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));

    }

    @Test
    void updateCustomer_ShouldThrowNotFoundException_WhenCustomerDoesNotExist() {

        Long invalidId = 99L; // Ett ID som inte finns
        UpdateCustomerRequest request = new UpdateCustomerRequest("Bilbo", "Bagger", "070-999");

        when(customerRepository.findById(invalidId))
                .thenReturn(empty());

        // Vi kontrollerar att rätt fel kallas, samt kollar felmeddelandet
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> customerService.updateCustomerById(invalidId, request)
        );

        assertEquals("Kunden hittades inte", exception.getMessage());

        // VIKTIG VERIFIERING: Eftersom kunden inte fanns, ska .save() ALDRIG ha anropats!
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer() {
    }
}