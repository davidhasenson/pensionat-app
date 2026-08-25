package org.example.pensionatapp.pensionat.customer.client;

public record CustomerDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}