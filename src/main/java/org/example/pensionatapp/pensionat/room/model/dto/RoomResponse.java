package org.example.pensionatapp.pensionat.room.model.dto;

public record RoomResponse(
        Long id,
        String roomNumber,
        int beds,
        int pricePerNight,
        String bedType
) {
}