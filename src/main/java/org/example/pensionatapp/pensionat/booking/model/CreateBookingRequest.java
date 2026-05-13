package org.example.pensionatapp.pensionat.booking.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingRequest(
        @NotNull(message = "Kund id måste anges")
        Long customerId,

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
