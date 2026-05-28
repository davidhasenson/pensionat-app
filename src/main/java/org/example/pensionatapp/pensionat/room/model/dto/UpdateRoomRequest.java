package org.example.pensionatapp.pensionat.room.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.pensionatapp.pensionat.room.enumeration.BedType;

public record UpdateRoomRequest(

        @NotBlank(message = "Rumsnummer måste anges")
        String roomNumber,

        @Min(value = 1, message = "Ett rum måste ha minst en säng")
        int beds,

        @NotNull(message = "Måste finnas minst en typ av säng i rummet")
        BedType bedType,

        @Min(value = 1, message = "Pris per natt måste vara större än 0")
        int pricePerNight
) {
}