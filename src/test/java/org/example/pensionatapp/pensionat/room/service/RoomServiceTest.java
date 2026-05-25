package org.example.pensionatapp.pensionat.room.service;

import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.model.Customer;
import org.example.pensionatapp.pensionat.error.BadRequestException;
import org.example.pensionatapp.pensionat.room.BedType;
import org.example.pensionatapp.pensionat.room.model.CreateRoomRequest;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository  bookingRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room1;
    private Room room2;
    private Room room3;

    List<Room> fakeRooms;

    @BeforeEach
    void setUp() {
        room1 = new Room(
                "111",
                2,
                BedType.DOUBLE_BED,
                1200);
        room1.setId(1L);

        room2 = new Room(
                "112",
                1,
                BedType.SINGLE_BED,
                800);
        room2.setId(2L);

        room3 = new Room(
                "113",
                2,
                BedType.TWIN_ROOM,
                1500);
        room3.setId(3L);

        fakeRooms = List.of(room1, room2, room3);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllRooms_shouldReturnListOfRooms(){}

    @Test
    void createRoom_shouldSaveAndReturnRoom(){

        //Arrange
        CreateRoomRequest request = new CreateRoomRequest(
                "111",
                2,
                BedType.DOUBLE_BED,
                1200
        );

        when(roomRepository.save(any(Room.class))).thenReturn(room1);

        //Act
        Room result = roomService.createRoom(
                request.roomNumber(),
                request.beds(),
                request.bedType(),
                request.pricePerNight()
        );

        //Assert
        assertNotNull(result, "Can't be null");
        assertEquals(room1, result);

        verify(roomRepository, times(1)).save(any());


    }

    @Test
    void getRoomById_shouldReturnRoom(){}

    @Test
    void getRoomById_shouldThrowNotFoundException(){}

    @Test
    void updateRoom_shouldUpdateAndReturnRoom(){}

    @Test
    void deleteRoom_shouldDeleteRoom(){
        //Arrange
        when(bookingRepository.findByRoomIdAndStatus(
                1L, BookingStatus.ACTIVE)).thenReturn(Collections.emptyList());

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room1));

        //Act
        roomService.deleteRoom(1L);

        //Assert
        verify(roomRepository, times(1)).delete(room1);
    }

    @Test
    void deleteRoom_shouldThrowBadRequestExceptionWhenActiveBookingsExist(){
        //Arrange
        Booking fakeBooking = new Booking(
                new Customer("Adam", "Klarin",
                        "adam@test.se", "07045356534"),
                room1,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                BookingStatus.ACTIVE
        );

        List<Booking> fakeBookings = List.of(fakeBooking);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room1));

        when(bookingRepository.findByRoomIdAndStatus(
                1L, BookingStatus.ACTIVE))
        .thenReturn(fakeBookings);

        //ACT + Assert

        assertThrows(BadRequestException.class, () -> roomService.deleteRoom(1L));
    }
}