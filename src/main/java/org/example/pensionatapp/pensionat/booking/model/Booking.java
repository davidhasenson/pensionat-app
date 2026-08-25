package org.example.pensionatapp.pensionat.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.example.pensionatapp.pensionat.booking.enumeration.BookingStatus;
import org.example.pensionatapp.pensionat.room.model.Room;

import java.time.LocalDate;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(optional = false)
    private Room room;

    @NotNull(message = "Startdatum måste anges")
    @FutureOrPresent(message = "Startdatum kan inte vara bakåt i tiden")
    private LocalDate startDate;

    @NotNull(message = "Slutdatum måste anges")
    @FutureOrPresent(message = "Slutdatum kan inte vara bakåt i tiden")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(columnDefinition = "boolean default false")
    private boolean extraBedRequested = false;

    protected Booking() {
    }

    public Booking(Long customerId, Room room, LocalDate startDate, LocalDate endDate, BookingStatus status, boolean extraBedIncluded) {
        this.customerId = customerId;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.extraBedRequested = extraBedIncluded;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public boolean isExtraBedRequested() {
        return extraBedRequested;
    }

    public void setExtraBedRequested(boolean extraBedRequested) {
        this.extraBedRequested = extraBedRequested;
    }
}