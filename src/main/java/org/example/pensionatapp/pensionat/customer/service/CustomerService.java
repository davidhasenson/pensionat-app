package org.example.pensionatapp.pensionat.customer.service;

import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.CreateCustomerRequest;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.model.UpdateCustomerRequest;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public CustomerService(CustomerRepository customerRepository, BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Kunden hittades inte")
        );
    }

    @Transactional
    public Customer createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer(request.firstName(), request.lastName(), request.email(), request.phone(), request.password());
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kunden hittades inte"));

        existingCustomer.setFirstName(request.firstName());
        existingCustomer.setLastName(request.lastName());
        existingCustomer.setPhone(request.phone());

        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kunden hittades inte"));

        boolean hasBooking = bookingRepository.existsByCustomerIdAndEndDateAfter(id, LocalDateTime.now());
        if (hasBooking) {
            throw new IllegalStateException("Kunden har aktiva bokningar");
        }

        customerRepository.delete(customer);
    }

}
