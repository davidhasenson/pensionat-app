package org.example.pensionatapp.pensionat;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.pensionatapp.jwt.service.JwtService;
import org.example.pensionatapp.pensionat.booking.model.dto.CreateBookingRequest;
import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
import org.example.pensionatapp.pensionat.customer.client.CustomerClient;
import org.example.pensionatapp.pensionat.customer.client.CustomerDto;
import org.example.pensionatapp.pensionat.room.enumeration.BedType;
import org.example.pensionatapp.pensionat.room.model.Room;
import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = BookingApiTest.EnvLoader.class)
public class BookingApiTest {

    static class EnvLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(entry -> System.setProperty(
                    entry.getKey(), entry.getValue()));
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private CustomerClient customerClient;

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "frodo")
    void createBooking() throws Exception {
        Room room = new Room("101", 1, BedType.SINGLE_BED, 500);
        Room savedRoom = roomRepository.save(room);

        when(customerClient.findByEmail("frodo@shire.com"))
                .thenReturn(Optional.of(
                        new CustomerDto(
                                1L,
                                "frodo",
                                "Frodo",
                                "Baggins",
                                "frodo@shire.com",
                                "0701234567"
                        )
                ));

        CreateBookingRequest request = new CreateBookingRequest(
                "frodo@shire.com",
                savedRoom.getId(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                false
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerEmail").value("frodo@shire.com"));
    }

    @Test
    void createBookingOnAlreadyBookedRoomReturns409Conflict() throws Exception {
        Room room = roomRepository.save(new Room("201", 2, BedType.DOUBLE_BED, 1000));

        CustomerDto customer = new CustomerDto(1L, "frodo123", "Frodo", "Baggins", "frodo@shire.com", "0701234567");
        when(customerClient.findByEmail(anyString())).thenReturn(Optional.of(customer));

        String token = "Bearer " + jwtService.generateToken("frodo123");

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(10);

        CreateBookingRequest firstRequest = new CreateBookingRequest(
                "frodo@shire.com", room.getId(), startDate, endDate, false
        );

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        CreateBookingRequest secondRequest = new CreateBookingRequest(
                "frodo@shire.com", room.getId(), startDate, endDate, false
        );

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict());
    }
}