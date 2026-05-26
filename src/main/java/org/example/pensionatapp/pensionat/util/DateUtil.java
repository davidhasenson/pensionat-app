package org.example.pensionatapp.pensionat.util;

import org.example.pensionatapp.pensionat.error.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private DateUtil() {
    }
    public static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            logger.warn("Validation failed: Start date or end date is missing");
            throw new BadRequestException("Startdatum och slutdatum måste anges");
        }
        if (startDate.isBefore(LocalDate.now())) {
            logger.warn("Validation failed: Start date {} is in the past", startDate);
            throw new BadRequestException("Startdatum kan inte vara bakåt i tiden");
        }
        if (startDate.isAfter(endDate)) {
            logger.warn("Validation failed: End date {} must be after start date {}", endDate, startDate);
            throw new BadRequestException("Slutdatum måste vara efter startdatum");
        }
    }
}
