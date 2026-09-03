package org.example.pensionatapp.pensionat;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.pensionatapp.pensionat.booking.model.dto.BookingResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
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
    private RestClient.Builder restClientBuilder;

    @MockitoBean
    private CustomerClient customerClient;

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "frodo")
    void createBooking() throws  Exception {
        Room room = new Room("101", 1, BedType.SINGLE_BED, 500);
        Room savedRoom = roomRepository.save(room);

        RestTemplate restTemplate = new RestTemplate();

        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();


        server.expect(requestTo("http://localhost:8081/api/customers/by-email?email=frodo@shire.com"))
                .andRespond(
                        withSuccess()
                                .body("{\"id\": 1,\n" +
                                        "\"userName\": \"frodo\",\n" +
                                        "\"firstName\": \"Frodo\",\n" +
                                        "\"lastName\": \"Baggins\",\n" +
                                        "\"email\": \"frodo@shire.com\",\n" +
                                        "\"phone\": \"0701234567\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        CreateBookingRequest request = new CreateBookingRequest(
                "frodo@shire.com",
                savedRoom.getId(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                false
        );

//        BookingResponse bs = restTemplate.postForObject(
//                "http://localhost:8080/api/bookings",
//                request,
//                BookingResponse.class);
//
//        assertEquals(HttpStatus.CREATED.value(),bs.status());
//        assertEquals(bs.customerEmail(), "frodo@shire.com");
//        assertNotNull(bs);
//        server.verify();


        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerEmail").value("frodo@shire.com"));
    }




}
//
//package org.example.pensionatapp.pensionat;
//
//import io.github.cdimascio.dotenv.Dotenv;
//import org.example.pensionatapp.pensionat.booking.model.dto.CreateBookingRequest;
//import org.example.pensionatapp.pensionat.booking.repository.BookingRepository;
//import org.example.pensionatapp.pensionat.customer.client.CustomerClient;
//import org.example.pensionatapp.pensionat.customer.client.CustomerDto;
//import org.example.pensionatapp.pensionat.room.enumeration.BedType;
//import org.example.pensionatapp.pensionat.room.model.Room;
//import org.example.pensionatapp.pensionat.room.repository.RoomRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.context.ApplicationContextInitializer;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import tools.jackson.databind.ObjectMapper;
//
//import java.time.LocalDate;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@ContextConfiguration(initializers = BookingApiTest.EnvLoader.class)
//public class BookingApiTest {
//
//    static class EnvLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//        @Override
//        public void initialize(ConfigurableApplicationContext applicationContext) {
//            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
//            dotenv.entries().forEach(entry -> System.setProperty(
//                    entry.getKey(), entry.getValue()));
//        }
//    }
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private tools.jackson.databind.ObjectMapper objectMapper;

//    @Autowired
//    private BookingRepository bookingRepository;
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @MockitoBean
//    private CustomerClient customerClient;
//
//    @AfterEach
//    void tearDown() {
//        bookingRepository.deleteAll();
//        roomRepository.deleteAll();
//    }
//
//    @Test
//    @WithMockUser(username = "frodo")
//    void createBooking() throws Exception {
//        Room room = new Room("101", 1, BedType.SINGLE_BED, 500);
//        Room savedRoom = roomRepository.save(room);
//
//        // Om din CustomerClient är ett Spring-bön (t.ex. FeignClient eller RestClient-wrapper)
//        // kan du mocka dess svars-metod här istället för MockRestServiceServer:
//
//        when(customerClient.getCustomerByEmail("frodo@shire.com"))
//                .thenReturn(new CustomerDto(1L, "frodo", "Frodo", "Baggins", "frodo@shire.com", "0701234567"));
//
//
//        CreateBookingRequest request = new CreateBookingRequest(
//                "frodo@shire.com",
//                savedRoom.getId(),
//                LocalDate.now().plusDays(1),
//                LocalDate.now().plusDays(5),
//                false
//        );
//
//        mockMvc.perform(post("/api/bookings")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.customerEmail").value("frodo@shire.com"));
//    }
//}