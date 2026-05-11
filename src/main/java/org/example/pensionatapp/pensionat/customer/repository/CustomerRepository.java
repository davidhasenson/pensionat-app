package org.example.pensionatapp.pensionat.customer.repository;

import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
