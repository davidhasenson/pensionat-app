package org.example.pensionatapp.pensionat.room.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateRoomRequest(

        @NotBlank(message = "Rumsnummer måste anges")
        String roomNumber,

        @Min(value = 1, message = "Ett rum måste ha minst en säng")
        int beds,

        @Min(value = 1, message = "Pris per natt måste vara större än 0")
        int pricePerNight

) {}
