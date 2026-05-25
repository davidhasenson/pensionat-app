package org.example.pensionatapp.pensionat.booking.controller;

import jakarta.validation.Valid;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.model.CreateBookingRequest;
import org.example.pensionatapp.pensionat.booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }
  
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestBody @Valid CreateBookingRequest request) {
        return bookingService.createBooking(
                request.customerEmail(),
                request.roomId(),
                request.startDate(),
                request.endDate()
        );
    }

    @PutMapping("/{id}")
    public Booking updateBooking(
            @PathVariable Long id,
            @RequestBody @Valid CreateBookingRequest request
    ) {
        return bookingService.updateBooking(
                id,
                request.roomId(),
                request.startDate(),
                request.endDate()
        );
    }

    @PatchMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }
}