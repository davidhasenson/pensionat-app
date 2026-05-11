package org.example.pensionatapp.pensionat.room.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Rumsnummer måste anges")
    private String roomNumber;

   @Min(value = 1, message = "Ett rum måste ha minst en säng")
    private int beds;

   @Min(value = 1, message = "Pris per natt måste vara större än 0")
    private int pricePerNight;

   protected Room() {}

    public Room(String roomNumber, int beds, int pricePerNight) {
        this.roomNumber = roomNumber;
        this.beds = beds;
        this.pricePerNight = pricePerNight;
    }
}

