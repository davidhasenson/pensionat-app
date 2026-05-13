package org.example.pensionatapp.pensionat.booking.repository;

import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
            Long roomId,
            BookingStatus status,
            LocalDate endDate,
            LocalDate startDate
    );
}