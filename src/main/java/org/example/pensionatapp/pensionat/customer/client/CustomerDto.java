package org.example.pensionatapp.pensionat.customer.client;

public record CustomerDto(
        Long id,
        String userName,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}