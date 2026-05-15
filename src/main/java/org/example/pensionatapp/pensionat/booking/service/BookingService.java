package org.example.pensionatapp.pensionat.booking.service;

import jakarta.transaction.Transactional;
import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    public BookingService(
            BookingRepository bookingRepository,
            CustomerRepository customerRepository,
            RoomRepository roomRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public Booking createBooking(Long customerId, Long roomId, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Kunden finns inte"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Rummet finns inte"));

        checkRoomAvailability(roomId, startDate, endDate);

        Booking booking = new Booking(customer, room, startDate, endDate, BookingStatus.ACTIVE);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long bookingId, Long roomId, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Bokningen finns inte"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Rummet finns inte"));

        checkRoomAvailability(roomId, startDate, endDate);

        booking.setRoom(room);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);

        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Bokningen finns inte"));

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Startdatum och slutdatum måste anges");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Startdatum kan inte vara bakåt i tiden");
        }

        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("Slutdatum måste vara efter startdatum");
        }
    }

    private void checkRoomAvailability(Long roomId, LocalDate startDate, LocalDate endDate) {
        boolean roomIsBooked = bookingRepository
                .existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
                        roomId,
                        BookingStatus.ACTIVE,
                        endDate,
                        startDate
                );

        if (roomIsBooked) {
            throw new IllegalStateException("Rummet är redan bokat under valt datumintervall");
        }
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bokningen finns inte"));
    }
}