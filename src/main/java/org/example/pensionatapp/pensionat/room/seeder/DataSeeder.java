package org.example.pensionatapp.pensionat.room.seeder;

import org.example.pensionatapp.pensionat.room.BedType;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoomRepository roomRepository;

    public DataSeeder(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roomRepository.count() == 0) {
            roomRepository.save(new Room("101", 1, BedType.SINGLE_BED, 800));
            roomRepository.save(new Room("102", 1, BedType.SINGLE_BED, 800));
            roomRepository.save(new Room("103", 1, BedType.SINGLE_BED, 800));
            roomRepository.save(new Room("104", 2, BedType.DOUBLE_BED, 800));
            roomRepository.save(new Room("105", 2,BedType.DOUBLE_BED, 1200));
            roomRepository.save(new Room("106", 2, BedType.DOUBLE_BED , 1200));
            roomRepository.save(new Room("107", 2, BedType.TWIN_ROOM, 1200));
            roomRepository.save(new Room("108", 3, BedType.SUITE, 2000));
            roomRepository.save(new Room("109", 3, BedType.SUITE, 2000));
            roomRepository.save(new Room("110", 3, BedType.SUITE, 2000));
            roomRepository.save(new Room("111", 4, BedType.SUITE, 3000));
        }
    }


}
