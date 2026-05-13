package org.example.pensionatapp.pensionat.room.service;

import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room createRoom (String roomNumber, int beds, int pricePerNight) {

        Room room = new Room (roomNumber, beds, pricePerNight);

        return roomRepository.save(room);
    }

    public Room getRoomById (long id) {
         return roomRepository.findById(id).orElseThrow(()
                 ->new NotFoundException("Rum med id " + id + " hittades inte."));
    }
}
