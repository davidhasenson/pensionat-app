package org.example.pensionatapp.pensionat.booking.repository;

import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
            Long roomId,
            BookingStatus status,
            LocalDate endDate,
            LocalDate startDate
    );

    List<Booking> findByCustomerIdAndStatus(Long customerId, BookingStatus status);

    List<Booking> findByRoomAndStatus(Long roomId, BookingStatus status);

    boolean existsByCustomerId(Long customerId);

    boolean existsByCustomerIdAndEndDateAfter(Long customerId, LocalDateTime now);
}