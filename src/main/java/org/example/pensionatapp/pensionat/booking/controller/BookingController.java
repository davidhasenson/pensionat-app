package org.example.pensionatapp.pensionat.booking.controller;

import jakarta.validation.Valid;
import org.example.pensionatapp.pensionat.booking.model.BookingResponse;
import org.example.pensionatapp.pensionat.booking.model.CreateBookingRequest;
import org.example.pensionatapp.pensionat.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        logger.info("Received HTTP GET request to fetch booking with ID: {}", id);
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        logger.info("Received HTTP GET request to fetch all bookings.");
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        logger.info("Received HTTP POST request to create booking for customer email: {} and room ID: {}",
                request.customerEmail(), request.roomId());

        BookingResponse booking = bookingService.createBooking(
                request.customerEmail(),
                request.roomId(),
                request.startDate(),
                request.endDate(),
                request.extraBedRequested()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @RequestBody @Valid CreateBookingRequest request
    ) {
        logger.info("Received HTTP PUT request to update booking with ID: {}", id);

        BookingResponse booking = bookingService.updateBooking(
                id,
                request.roomId(),
                request.startDate(),
                request.endDate()
        );

        return ResponseEntity.ok(booking);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        logger.info("Received HTTP PATCH request to cancel booking with ID: {}", id);
        BookingResponse booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(booking);
    }
}