package org.example.pensionatapp.pensionat.room.service;

import org.example.pensionatapp.pensionat.booking.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.booking.service.BookingService;
import org.example.pensionatapp.pensionat.error.BadRequestException;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.example.pensionatapp.pensionat.room.BedType;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.model.RoomResponse;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.example.pensionatapp.pensionat.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room createRoom(String roomNumber, int beds, BedType bedType, int pricePerNight) {

        Room room = new Room(roomNumber, beds, bedType, pricePerNight);

        return roomRepository.save(room);
    }

    public Room getRoomById(long id) {
        return roomRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Rum med id " + id + " hittades inte."));
    }

    public Room updateRoom(long id, String roomNumber, int beds, BedType bedType, int pricePerNight) {

        Room room = getRoomById(id);

        room.setRoomNumber(roomNumber);
        room.setBeds(beds);
        room.setBedType(bedType);
        room.setPricePerNight(pricePerNight);

        return roomRepository.save(room);
    }

    public void deleteRoom(long id) {
        Room room = getRoomById(id);

        if (!bookingRepository.findByRoomIdAndStatus(id, BookingStatus.ACTIVE).isEmpty()) {
            throw new BadRequestException("Rummet har aktiva bokningar och kan inte tas bort.");
        }
        roomRepository.delete(room);
    }

    public List<RoomResponse> findAvailableRooms(LocalDate startDate, LocalDate endDate) {
        logger.info("Searching for available rooms between {} and {}", startDate, endDate);
        DateUtil.validateDates(startDate, endDate);

        List<Booking> bookings = bookingRepository.findByStatusAndStartDateLessThanAndEndDateGreaterThan(
                BookingStatus.ACTIVE, endDate, startDate
        );

        List<Room> bookedRooms = new ArrayList<>();
        for (Booking booking : bookings) {
            bookedRooms.add(booking.getRoom());
        }
        logger.info("Found {} occupied rooms during this period: {}", bookedRooms.size(), bookedRooms);

        List<Room> allRooms = roomRepository.findAll();
        allRooms.removeAll(bookedRooms);

        logger.info("Search completed. Returning {} available rooms", allRooms.size());
        return convertToRoomResponses(allRooms);
    }

    private List<RoomResponse> convertToRoomResponses(List<Room> rooms) {
        List<RoomResponse> responses = new ArrayList<>();
        for (Room room : rooms) {
            String bedType = "Okänd";
            if (room.getBedType() != null) {
                bedType = room.getBedType().getDisplayName();
            }

            responses.add(new RoomResponse(
                    room.getId(),
                    room.getRoomNumber(),
                    room.getBeds(),
                    room.getPricePerNight(),
                    bedType
            ));
        }
        return responses;
    }

}

