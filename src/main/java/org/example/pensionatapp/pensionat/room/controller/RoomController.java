package org.example.pensionatapp.pensionat.room.controller;

import jakarta.validation.Valid;
import org.example.pensionatapp.pensionat.room.model.DTO.CreateRoomRequest;
import org.example.pensionatapp.pensionat.room.model.DTO.RoomResponse;
import org.example.pensionatapp.pensionat.room.model.DTO.UpdateRoomRequest;
import org.example.pensionatapp.pensionat.room.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        logger.info("Received HTTP GET request to fetch all rooms");
        return ResponseEntity.ok( roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable long id) {
        logger.info("Received HTTP GET request to fetch room with ID: {}", id);
        return ResponseEntity.ok( roomService.getRoomById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        logger.info("Received HTTP POST request to create room");
        return ResponseEntity.ok(roomService.createRoom(
                request.roomNumber(),
                request.beds(),
                request.bedType(),
                request.pricePerNight()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable long id, @Valid @RequestBody UpdateRoomRequest request) {
        logger.info("Received HTTP PUT request to update room with ID: {}", id);
        return ResponseEntity.ok(roomService.updateRoom(
                id,
                request.roomNumber(),
                request.beds(),
                request.bedType(),
                request.pricePerNight()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable long id) {
        logger.info("Received HTTP DELETE request to delete room with ID: {}", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        logger.info("Received request to fetch available rooms between {} and {}", startDate, endDate);
        List<RoomResponse> availableRooms = roomService.findAvailableRooms(startDate, endDate);
        logger.info("Found {} available rooms for the period {} to {}", availableRooms.size(), startDate, endDate);
        return ResponseEntity.ok().body(availableRooms);
    }
}