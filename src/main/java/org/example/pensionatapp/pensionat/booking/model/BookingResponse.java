package org.example.pensionatapp.pensionat.booking.model;

import java.time.LocalDate;

public record BookingResponse(
        Long id,
        String customerEmail,
        String customerFirstName,
        String customerLastName,
        Long roomId,
        String roomNumber,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        boolean extraBedIncluded
) {
}