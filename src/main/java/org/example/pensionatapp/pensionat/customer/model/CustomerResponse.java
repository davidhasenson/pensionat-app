package org.example.pensionatapp.pensionat.customer.model;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}
