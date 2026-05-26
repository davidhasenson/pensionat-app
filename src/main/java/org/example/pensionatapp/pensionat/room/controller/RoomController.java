package org.example.pensionatapp.pensionat.room.controller;

import jakarta.validation.Valid;
import org.example.pensionatapp.pensionat.room.model.CreateRoomRequest;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.model.RoomResponse;
import org.example.pensionatapp.pensionat.room.model.UpdateRoomRequest;
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
    public List<RoomResponse> getAllRooms() {
        logger.info("Getting all rooms");
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable long id) {
        logger.info("Getting room with id {}", id);
        return roomService.getRoomById(id);
    }

    @PostMapping
    public Room createRoom(@RequestBody @Valid CreateRoomRequest request) {
        logger.info("Creating room {}", request);
        return roomService.createRoom(
                request.roomNumber(),
                request.beds(),
                request.bedType(),
                request.pricePerNight());
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable long id, @RequestBody @Valid UpdateRoomRequest request) {
        logger.info("Updating room {}", request);
        return roomService.updateRoom(
                id,
                request.roomNumber(),
                request.beds(),
                request.bedType(),
                request.pricePerNight());
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable long id) {
        logger.info("Deleting room {}", id);
        roomService.deleteRoom(id);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        logger.info("Received request to fetch available rooms between {} and {}", startDate, endDate);
        List<RoomResponse> availableRooms = roomService.findAvailableRooms(startDate, endDate);
        return ResponseEntity.ok().body(availableRooms);
    }

}
