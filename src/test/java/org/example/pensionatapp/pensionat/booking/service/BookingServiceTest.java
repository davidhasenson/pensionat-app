package org.example.pensionatapp.pensionat.booking.service;

import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.model.BookingResponse;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.customer.repository.CustomerRepository;
import org.example.pensionatapp.pensionat.error.BadRequestException;
import org.example.pensionatapp.pensionat.room.BedType;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    private Customer customer;
    private Room room;
    private Booking booking;

    @BeforeEach
    void setUp() {
        customer = new Customer(
                "Frodo",
                "Bagger",
                "frodo@pensionat.se",
                "070-111"
        );
        customer.setId(1L);

        room = new Room(
                "111",
                2,
                BedType.DOUBLE_BED,
                1200
        );
        room.setId(1L);

        booking = new Booking(
                customer,
                room,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                BookingStatus.ACTIVE,
                false
        );
    }

    @Test
    void createBooking_shouldSaveAndReturnBooking() {
        String customerEmail = "test@test.se";
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        boolean extraBedRequested = false;

        when(customerRepository.findByEmail(customerEmail)).thenReturn(Optional.of(customer));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        when(bookingRepository.existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
                roomId,
                BookingStatus.ACTIVE,
                endDate,
                startDate
        )).thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse result = bookingService.createBooking(customerEmail, roomId, startDate, endDate, extraBedRequested);
        assertNotNull(result, "Bokningen borde inte vara null");
        assertEquals("frodo@pensionat.se", result.customerEmail(), "Kundens e-post matchar inte");
        assertEquals("Frodo", result.customerFirstName(), "Förnamnet matchar inte");
        assertEquals("Bagger", result.customerLastName(), "Efternamnet matchar inte");
        assertEquals(1L, result.roomId(), "Rummets ID matchar inte");
        assertEquals("111", result.roomNumber(), "Rumsnumret matchar inte");
        assertEquals("ACTIVE", result.status(), "Status borde vara ACTIVE");
        assertFalse(result.extraBedIncluded(), "Extrasäng borde vara false");

        verify(customerRepository, times(1)).findByEmail(customerEmail);
        verify(roomRepository, times(1)).findById(roomId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowException_WhenRoomIsAlreadyBooked() {
        String customerEmail = "test@teat.se";
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        boolean extraBedRequested = false;

        when(customerRepository.findByEmail(customerEmail)).thenReturn(Optional.of(customer));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        when(bookingRepository.existsByRoomIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
                roomId,
                BookingStatus.ACTIVE,
                endDate,
                startDate
        )).thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(customerEmail, roomId, startDate, endDate,extraBedRequested),
                "Dubbelbokning borde kasta ett fel"
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }
}