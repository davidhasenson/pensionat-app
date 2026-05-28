package org.example.pensionatapp.pensionat.room.service;

import org.example.pensionatapp.pensionat.booking.enumeration.BookingStatus;
import org.example.pensionatapp.pensionat.booking.model.Booking;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.error.BadRequestException;
import org.example.pensionatapp.pensionat.error.NotFoundException;
import org.example.pensionatapp.pensionat.room.enumeration.BedType;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.model.dto.RoomResponse;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
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

    public List<RoomResponse> getAllRooms() {

        logger.info("Fetching all rooms");
        List<Room> rooms = roomRepository.findAll();

        logger.info("Rooms found");

        return convertToRoomResponses(rooms);
    }

    public RoomResponse createRoom(String roomNumber, int beds, BedType bedType, int pricePerNight) {
        logger.info("Creating room with number: {} ",roomNumber);
        Room room = new Room(roomNumber, beds, bedType, pricePerNight);
        Room savedRoom = roomRepository.save(room);
        logger.info("Created room with number: {}", roomNumber);
        return toRoomResponse(savedRoom);
    }

    public RoomResponse getRoomById(long id) {
        logger.info("Fetching after id: {}", id);
        Room room = roomRepository.findById(id).orElseThrow(()
                -> {
                logger.warn("Room with id {} not found", id);
                return new NotFoundException("Rum med id " + id + " hittades inte.");
        });
        return toRoomResponse(room);
    }

    public RoomResponse updateRoom(long id, String roomNumber, int beds, BedType bedType, int pricePerNight) {

        logger.info("Updating room with id: {} ", id);
        Room room = findRoomById(id);

        room.setRoomNumber(roomNumber);
        room.setBeds(beds);
        room.setBedType(bedType);
        room.setPricePerNight(pricePerNight);
        Room updatedRoom = roomRepository.save(room);

        logger.info("Updated room with id: {} ", id);
        return toRoomResponse(updatedRoom);
    }

    public void deleteRoom(long id) {
        logger.info("Deleting room with id: {}", id);
        Room room = findRoomById(id);

        if (!bookingRepository.findByRoomIdAndStatus(id, BookingStatus.ACTIVE).isEmpty()) {
            logger.warn("Room with id{} has already been booked", id);
            throw new BadRequestException("Rummet har aktiva bokningar och kan inte tas bort.");
        }
        logger.info("Deleted room with id: {} ", id);
        roomRepository.delete(room);
    }

    public List<RoomResponse> findAvailableRooms(LocalDate startDate, LocalDate endDate) {
        logger.info("Searching for available rooms between {} and {}", startDate, endDate);
        validateDates(startDate, endDate);

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
    public void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            logger.warn("Validation failed: Start date or end date is missing");
            throw new BadRequestException("Startdatum och slutdatum måste anges");
        }
        if (startDate.isBefore(LocalDate.now())) {
            logger.warn("Validation failed: Start date {} is in the past", startDate);
            throw new BadRequestException("Startdatum kan inte vara bakåt i tiden");
        }
        if (startDate.isAfter(endDate)) {
            logger.warn("Validation failed: End date {} must be after start date {}", endDate, startDate);
            throw new BadRequestException("Slutdatum måste vara efter startdatum");
        }
    }

    private RoomResponse toRoomResponse(Room room) {
        return convertToRoomResponses(List.of(room)).get(0);
    }

    private Room findRoomById(long id) {
        logger.info("Finding room with id: {}", id);
        return roomRepository.findById(id).orElseThrow(()
        -> {
                    logger.warn("Room with id{} not found", id);
                    return new NotFoundException("Rummet med id " + id + " hittades inte");

        } );
    }
}