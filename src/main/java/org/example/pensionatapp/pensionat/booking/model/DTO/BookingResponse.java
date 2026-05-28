package org.example.pensionatapp.pensionat.booking.model.DTO;

import java.time.LocalDate;

public record BookingResponse(
        Long id,
        String customerEmail,
        String customerFirstName,
        String customerLastName,
        Long roomId,
        String roomNumber,
        String bedType,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        boolean extraBedIncluded
) {
}