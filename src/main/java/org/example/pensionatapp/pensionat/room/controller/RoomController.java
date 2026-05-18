package org.example.pensionatapp.pensionat.room.controller;

import jakarta.validation.Valid;
import org.example.pensionatapp.pensionat.room.model.CreateRoomRequest;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.model.UpdateRoomRequest;
import org.example.pensionatapp.pensionat.room.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAllRooms(){
        return roomService.getAllRooms();
    }

    @GetMapping ("/{id}")
    public Room getRoomById(@PathVariable long id){
        return roomService.getRoomById(id);
    }

    @PostMapping
    public Room createRoom(@RequestBody @Valid CreateRoomRequest request){
        return roomService.createRoom(
                request.roomNumber(),
                request.beds(),
                request.pricePerNight());
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable long id, @RequestBody @Valid UpdateRoomRequest request){
        return roomService.updateRoom(
                id,
                request.roomNumber(),
                request.beds(),
                request.pricePerNight());
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable long id){
        roomService.deleteRoom(id);
    }

}
