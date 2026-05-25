package org.example.pensionatapp.pensionat.customer.service;

import jakarta.transaction.Transactional;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.CreateCustomerRequest;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.model.UpdateCustomerRequest;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public CustomerService(CustomerRepository customerRepository, BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Customer> getAllCustomers() {
        logger.info("Fetching all customers from the database");
        List<Customer> customers = customerRepository.findAll();
        logger.info("Successfully retrieved {} customers", customers.size());
        return customers;
    }

    public Customer getCustomerById(Long id) {
        logger.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id).orElseThrow(
                () -> {
                    logger.warn("Fetch failed: Customer with ID {} not found", id);
                    return new NotFoundException("Kunden hittades inte");
                }
        );
    }

    public Customer getCustomerByEmail(String email) {
        //   logger.info("Fetching customer with email: {}", email);
        return customerRepository.findByEmail(email).orElseThrow(
                () -> {
                    //     logger.warn("Fetch failed: Customer with email {} not found", email);
                    return new NotFoundException("Kunden hittades inte");
                }
        );
    }

    @Transactional
    public Customer createCustomer(CreateCustomerRequest request) {
        logger.info("Attempting to create a new customer with email: {}", request.email());
        Customer customer = new Customer(request.firstName(), request.lastName(), request.email(), request.phone());
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer successfully created with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    @Transactional
    public Customer updateCustomer(Long id, UpdateCustomerRequest request) {
        logger.info("Attempting to update customer with ID: {}", id);
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Update failed: Customer with ID {} not found", id);
                    return new NotFoundException("Kunden hittades inte");
                });

        existingCustomer.setFirstName(request.firstName());
        existingCustomer.setLastName(request.lastName());
        existingCustomer.setPhone(request.phone());

        logger.info("Customer details successfully updated for ID: {}", id);
        return customerRepository.save(existingCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        logger.info("Attempting to delete customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Delete failed: Customer with ID {} not found", id);
                    return new NotFoundException("Kunden hittades inte");
                });

        boolean hasBooking = bookingRepository.existsByCustomerIdAndEndDateAfter(id, LocalDateTime.now());
        if (hasBooking) {
            logger.warn("Delete failed: Customer with ID {} has active bookings", id);
            throw new IllegalStateException("Kunden har aktiva bokningar");
        }

        logger.info("Unlinking past bookings for customer ID: {}", id);
        for (Booking booking : bookingRepository.findByCustomerId(id)) {
            booking.setCustomer(null);
            bookingRepository.save(booking);
        }

        customerRepository.delete(customer);
        logger.info("Customer with ID {} was successfully deleted", id);
    }

}
