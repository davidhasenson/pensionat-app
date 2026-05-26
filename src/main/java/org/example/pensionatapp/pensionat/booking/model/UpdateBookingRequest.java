package org.example.pensionatapp.pensionat.booking.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateBookingRequest(
        @NotNull(message = "Rums id måste anges")
        Long roomId,

        @NotNull(message = "Startdatum måste anges")
        LocalDate startDate,

        @NotNull(message = "Slutdatum måste anges")
        LocalDate endDate,

        boolean extraBedRequested
) {}
