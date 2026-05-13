package org.example.pensionatapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PensionatAppApplication {

    public static void main(String[] args) {
        // Laddar .env-filen
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Gör variablerna tillgängliga som system-properties för Spring
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(PensionatAppApplication.class, args);
    }
}

/*TODO
 * create BookingService
 * create RoomService
 * create Customer Controller
 * create Booking Controller
 * */