package org.example.pensionatapp.pensionat.room.service;

import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
