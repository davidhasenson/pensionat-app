package org.example.pensionatapp.pensionat.booking.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingRequest(
        @NotBlank(message = "Kundens e-postadress måste anges")
        @Email(message = "Ange en giltig e-postadress")
        String customerEmail,

        @NotNull(message = "Rum id måste anges")
        Long roomId,

        @NotNull(message = "Startdatum måste anges")
        //@JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @NotNull(message = "Slutdatum måste anges")
        //@JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate

) {
}