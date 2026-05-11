package org.example.pensionatapp.pensionat.room.repository;

import org.example.pensionatapp.pensionat.room.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
