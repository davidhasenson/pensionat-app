package org.example.pensionatapp.pensionat.booking.service;

import jakarta.transaction.Transactional;
import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.model.BookingResponse;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.example.pensionatapp.pensionat.error.BadRequestException;
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
    public BookingResponse createBooking(
            String customerEmail,
            Long roomId,
            LocalDate startDate,
            LocalDate endDate,
            boolean extraBedRequested
    ) {
        validateDates(startDate, endDate);

        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new NotFoundException("Ingen kund hittades med e-postadressen: " + customerEmail));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Rummet finns inte"));
        
        if (extraBedRequested && room.getBedType() != BedType.DOUBLE_BED) {
            throw new BadRequestException("Extrasäng kan endast bokas i dubbelrum.");
        }

        checkRoomAvailability(roomId, startDate, endDate);

        Booking booking = new Booking(customer, room, startDate, endDate, BookingStatus.ACTIVE, extraBedRequested);

        return convertToBookingResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse updateBooking(Long bookingId, Long roomId, LocalDate startDate, LocalDate endDate) {
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

        return convertToBookingResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Bokningen finns inte"));

        booking.setStatus(BookingStatus.CANCELLED);

        return convertToBookingResponse(bookingRepository.save(booking));
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToBookingResponse)
                .toList();
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bokningen finns inte"));

        return convertToBookingResponse(booking);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Startdatum och slutdatum måste anges");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Startdatum kan inte vara bakåt i tiden");
        }

        if (!startDate.isBefore(endDate)) {
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

    private BookingResponse convertToBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getEmail(),
                booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName(),
                booking.getRoom().getId(),
                booking.getRoom().getRoomNumber(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus().name(),
                booking.isExtraBedIncluded()
        );
    }
}
