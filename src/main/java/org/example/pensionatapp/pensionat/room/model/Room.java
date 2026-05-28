package org.example.pensionatapp.pensionat.room.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.example.pensionatapp.pensionat.room.enumeration.BedType;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Rumsnummer måste anges")
    private String roomNumber;

   @Min(value = 1, message = "Ett rum måste ha minst en säng")
    private int beds;

   @Enumerated(EnumType.STRING)
   private BedType bedType;

   @Min(value = 0, message = "Pris per natt måste vara större än 0")
    private int pricePerNight;

   protected Room() {}

    public Room(String roomNumber, int beds, int pricePerNight) {
       this.roomNumber = roomNumber;
       this.beds = beds;
       this.pricePerNight = pricePerNight;
    }

    public Room(String roomNumber, int beds, BedType bedType, int pricePerNight) {
        this.roomNumber = roomNumber;
        this.beds = beds;
        this.bedType = bedType;
        this.pricePerNight = pricePerNight;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public BedType getBedType() {
       return bedType;
    }

    public void setBedType(BedType bedType) {
       this.bedType = bedType;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(int pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
}