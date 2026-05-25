package org.example.pensionatapp.pensionat.room.model;

public record RoomResponse(
        Long id,
        String roomNumber,
        int beds,
        int pricePerNight,
        String bedType
) {}
