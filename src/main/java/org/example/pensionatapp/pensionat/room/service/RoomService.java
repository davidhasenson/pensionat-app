package org.example.pensionatapp.pensionat.room.service;

import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.error.BadRequestException;
import org.example.pensionatapp.pensionat.error.NotFoundException;
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

    public Room createRoom (String roomNumber, int beds, int pricePerNight) {

        Room room = new Room (roomNumber, beds, pricePerNight);

        return roomRepository.save(room);
    }

    public Room getRoomById (long id) {
         return roomRepository.findById(id).orElseThrow(()
                 ->new NotFoundException("Rum med id " + id + " hittades inte."));
    }

    public Room updateRoom (long id, String roomNumber, int beds, int pricePerNight) {

       Room room = getRoomById(id);

        room.setRoomNumber(roomNumber);
        room.setBeds(beds);
        room.setPricePerNight(pricePerNight);

        return roomRepository.save(room);
    }

    public void deleteRoom (long id) {
        Room room = getRoomById(id);

       if (!bookingRepository.findByRoomIdAndStatus(id, BookingStatus.ACTIVE).isEmpty()){
            throw new BadRequestException("Rummet har aktiva bokningar och kan inte tas bort.");
       }
            roomRepository.delete(room);
    }
}
