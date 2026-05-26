package org.example.pensionatapp.pensionat.booking.service;

import jakarta.transaction.Transactional;
import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.example.pensionatapp.pensionat.room.BedType;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.example.pensionatapp.pensionat.error.BadRequestException;

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
    public Booking createBooking(String customerEmail, Long roomId, LocalDate startDate, LocalDate endDate,boolean extraBedRequested) {
        validateDates(startDate, endDate);

        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new NotFoundException("Ingen kund hittades med e-postadressen: " + customerEmail));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Rummet finns inte"));

        if (extraBedRequested && room.getBedType() != BedType.DOUBLE_BED) {
            throw new BadRequestException("Extrasäng kan endast bokas i dubbelrum.");
        }

        checkRoomAvailability(roomId, startDate, endDate);

        Booking booking = new Booking(customer, room, startDate, endDate, BookingStatus.ACTIVE,extraBedRequested);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long bookingId, Long roomId, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Bokningen finns inte"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Rummet finns inte"));

        boolean roomIsBooked = bookingRepository
                .existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThanAndIdNot(
                        roomId,
                        BookingStatus.ACTIVE,
                        endDate,
                        startDate,
                        bookingId
                );

        if (roomIsBooked) {
            throw new BadRequestException("Rummet är redan bokat under valt datumintervall");
        }

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
          //  logger.warn("Validation failed: Start date or end date is missing");
            throw new BadRequestException("Startdatum och slutdatum måste anges");
        }
        if (startDate.isBefore(LocalDate.now())) {
          //  logger.warn("Validation failed: Start date {} is in the past", startDate);
            throw new BadRequestException("Startdatum kan inte vara bakåt i tiden");
        }
        if (!startDate.isBefore(endDate)) {
         //   logger.warn("Validation failed: End date {} must be after start date {}", endDate, startDate);
            throw new BadRequestException("Slutdatum måste vara efter startdatum");
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
            throw new BadRequestException("Rummet är redan bokat under valt datumintervall");
        }
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bokningen finns inte"));
    }
}