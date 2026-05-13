package org.example.pensionatapp.pensionat.room.utils;

import org.example.pensionatapp.pensionat.error.BadRequestException;

import java.time.LocalDate;

public class Validations {

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("Slutdatum måste vara efter startdatum");
        }
    }


}
