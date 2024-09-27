package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.practicum.shareit.booking.dto.BookingResponce;

@WebMvcTest(BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    BookingResponce bookingResponce;

    BookingRequest bookingRequest;

    static final String HEADER = "X-Sharer-User-Id";

    @BeforeEach
    public void createBeforeEach() {
        bookingResponce = new BookingResponce();
        bookingResponce.setId(1);
        bookingResponce.setStatus(Status.WAITING);

        bookingRequest = new BookingRequest();
        bookingRequest.setItemId(1);
        bookingRequest.setStart(LocalDateTime.now());
        bookingRequest.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    @Order(1)
    @DisplayName("BookingController_saveRequest")
    public void saveRequestTest() throws Exception {
        when(bookingService.saveRequest(any(BookingRequest.class), anyInt()))
                .thenReturn(bookingResponce);

        String bookingRequestJson = objectMapper.writeValueAsString(bookingRequest);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequestJson)
                        .header(HEADER, 1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @Order(2)
    @DisplayName("BookingController_approved")
    public void approvedTest() throws Exception {
        bookingResponce.setStatus(Status.APPROVED);

        when(bookingService.approved(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(bookingResponce);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @Order(3)
    @DisplayName("BookingController_findById")
    public void findByIdTest() throws Exception {
        when(bookingService.findById(anyInt(), anyInt()))
                .thenReturn(bookingResponce);

        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @Order(4)
    @DisplayName("BookingController_findAllByUserId")
    public void findAllByUserIdTest() throws Exception {
        when(bookingService.findAllByUserId(anyInt(), anyString()))
                .thenReturn(Collections.singletonList(bookingResponce));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @Order(4)
    @DisplayName("BookingController_findAllByOwnerId")
    public void findAllByOwnerIdTest() throws Exception {
        when(bookingService.findAllByOwnerId(anyInt(), anyString()))
                .thenReturn(Collections.singletonList(bookingResponce));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}