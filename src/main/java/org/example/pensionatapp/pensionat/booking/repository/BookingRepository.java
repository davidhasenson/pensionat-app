package org.example.pensionatapp.pensionat.booking.repository;

import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
