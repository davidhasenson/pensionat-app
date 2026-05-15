package org.example.pensionatapp.pensionat.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerRequest(
        @NotBlank(message = "Förnamn måste anges")
        String firstName,

        @NotBlank(message = "Efternamn måste anges")
        String lastName,

        String phone
) {
}
