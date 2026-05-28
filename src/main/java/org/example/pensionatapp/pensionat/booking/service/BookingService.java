package org.example.pensionatapp.pensionat.booking.service;

import jakarta.transaction.Transactional;
import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.model.DTO.BookingResponse;
import org.example.pensionatapp.pensionat.booking.model.DTO.UpdateBookingRequest;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.example.pensionatapp.pensionat.error.BadRequestException;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.example.pensionatapp.pensionat.room.BedType;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

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
    public BookingResponse createBooking(
            String customerEmail,
            Long roomId,
            LocalDate startDate,
            LocalDate endDate,
            boolean extraBedRequested
    ) {
        logger.info("Creating booking for customer email: {}, room ID: {}, dates: {} to {}",
                customerEmail, roomId, startDate, endDate);

        validateDates(startDate, endDate);

        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> {
                    logger.warn("Customer not found with email: {}", customerEmail);
                    return new NotFoundException("Ingen kund hittades med e-postadressen: " + customerEmail);
                });

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> {
                    logger.warn("Room not found with ID: {}", roomId);
                    return new NotFoundException("Rummet finns inte");
                });

        if (extraBedRequested && room.getBedType() != BedType.DOUBLE_BED) {
            throw new BadRequestException("Extrasäng kan endast bokas i dubbelrum.");
        }

        checkRoomAvailability(roomId, startDate, endDate);

        Booking booking = new Booking(customer, room, startDate, endDate, BookingStatus.ACTIVE, extraBedRequested);
        Booking savedBooking = bookingRepository.save(booking);

        logger.info("Booking created successfully with ID: {}", savedBooking.getId());

        return convertToBookingResponse(savedBooking);
    }

    @Transactional
    public BookingResponse updateBooking(Long bookingId, UpdateBookingRequest request) {
        logger.info("Attempting to update booking with ID: {}", bookingId);

        validateDates(request.startDate(), request.endDate());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.warn("Update failed: Booking not found with ID: {}", bookingId);
                    return new NotFoundException("Bokningen finns inte");
                });

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Det går inte att ändra en avbokad reservation.");
        }

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> {
                    logger.warn("Room not found with ID: {}", request.roomId());
                    return new NotFoundException("Det nya rummet hittades inte");
                });

        boolean roomIsBooked = bookingRepository
                .existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThanAndIdNot(
                        request.roomId(),
                        BookingStatus.ACTIVE,
                        request.endDate(),
                        request.startDate(),
                        bookingId
                );

        if (roomIsBooked) {
            logger.warn("Update failed. Room ID {} is already booked between {} and {}", request.roomId(), request.startDate(), request.endDate());
            throw new BadRequestException("Rummet är redan bokat under valt datumintervall");
        }

        if (request.extraBedRequested() && room.getBedType() != BedType.DOUBLE_BED) {
            throw new BadRequestException("Extrasäng kan endast bokas i dubbelrum.");
        }

        booking.setRoom(room);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setExtraBedRequested(request.extraBedRequested());

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking with ID {} updated successfully", savedBooking.getId());

        return convertToBookingResponse(savedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        logger.info("Cancelling booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.warn("Booking not found with ID: {}", bookingId);
                    return new NotFoundException("Bokningen finns inte");
                });

        booking.setStatus(BookingStatus.CANCELLED);

        Booking savedBooking = bookingRepository.save(booking);

        logger.info("Booking with ID {} cancelled successfully", savedBooking.getId());

        return convertToBookingResponse(savedBooking);
    }

    public List<BookingResponse> getAllBookings() {
        logger.info("Fetching all bookings");

        List<BookingResponse> bookings = bookingRepository.findAll()
                .stream()
                .map(this::convertToBookingResponse)
                .toList();

        logger.info("Returning {} bookings", bookings.size());

        return bookings;
    }

    public BookingResponse getBookingById(Long id) {
        logger.info("Fetching booking with ID: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Booking not found with ID: {}", id);
                    return new NotFoundException("Bokningen finns inte");
                });

        return convertToBookingResponse(booking);
    }

    public List<BookingResponse> getAllBookingsByEmail(String email) {
        logger.info("Fetching all bookings by email: {}", email);
        List<Booking> bookings = bookingRepository.findByCustomerEmail(email);
        List<BookingResponse> responses = new ArrayList<>();
        for (Booking booking : bookings) {
            responses.add(convertToBookingResponse(booking));
        }
        return responses;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            logger.warn("Validation failed: start date or end date is missing");
            throw new BadRequestException("Startdatum och slutdatum måste anges");
        }

        if (startDate.isBefore(LocalDate.now())) {
            logger.warn("Validation failed: start date {} is in the past", startDate);
            throw new BadRequestException("Startdatum kan inte vara bakåt i tiden");
        }

        if (!startDate.isBefore(endDate)) {
            logger.warn("Validation failed: end date {} must be after start date {}", endDate, startDate);
            throw new BadRequestException("Slutdatum måste vara efter startdatum");
        }
    }

    private void checkRoomAvailability(Long roomId, LocalDate startDate, LocalDate endDate) {
        logger.info("Checking room availability for room ID: {}, dates: {} to {}", roomId, startDate, endDate);

        boolean roomIsBooked = bookingRepository
                .existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
                        roomId,
                        BookingStatus.ACTIVE,
                        endDate,
                        startDate
                );

        if (roomIsBooked) {
            logger.warn("Room ID {} is already booked between {} and {}", roomId, startDate, endDate);
            throw new BadRequestException("Rummet är redan bokat under valt datumintervall");
        }
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getEmail(),
                booking.getCustomer().getFirstName(),
                booking.getCustomer().getLastName(),
                booking.getRoom().getId(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getBedType().getDisplayName(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus().name(),
                booking.isExtraBedRequested()
        );
    }
}