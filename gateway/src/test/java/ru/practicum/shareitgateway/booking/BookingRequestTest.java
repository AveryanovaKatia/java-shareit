package ru.practicum.shareitgateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareitgateway.ShareItGateway;
import ru.practicum.shareitgateway.booking.dto.BookingRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequestTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("BookingRequest_serializeJson")
    void serializeJsonTest() throws Exception {

        final BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setItemId(1);
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(3));

        String json = objectMapper.writeValueAsString(bookingRequest);
        assertThat(json).contains("\"itemId\":1");
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("BookingRequest_deserializeJson")
    void deserializeJsonTest() throws Exception {

        String json = "{\"itemId\":1,\"start\":\"2024-09-28T10:00:00\",\"end\":\"2024-09-29T10:00:00\"}";

        BookingRequest bookingRequest = objectMapper.readValue(json, BookingRequest.class);
        assertThat(bookingRequest.getItemId()).isEqualTo(1);
    }
}
