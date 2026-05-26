package org.example.pensionatapp.pensionat.booking.repository;

import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
            Long roomId,
            BookingStatus status,
            LocalDate endDate,
            LocalDate startDate
    );

    boolean existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThanAndIdNot(
            Long roomId,
            BookingStatus status,
            LocalDate endDate,
            LocalDate startDate,
            Long bookingId
    );

    boolean existsByCustomerIdAndStatus(
            Long customerId,
            BookingStatus status
    );

    List<Booking> findByCustomerIdAndStatus(
            Long customerId,
            BookingStatus status
    );

    List<Booking> findByRoomIdAndStatus(
            Long roomId,
            BookingStatus status
    );

    boolean existsByCustomerId(Long customerId);

    boolean existsByCustomerIdAndEndDateAfter(Long customerId, LocalDate date);

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByStatusAndStartDateLessThanAndEndDateGreaterThan(
            BookingStatus status,
            LocalDate endDate,
            LocalDate startDate
    );
}