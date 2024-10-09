package ru.practicum.shareitgateway.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequest {

    @NotBlank
    LocalDateTime start;

    @NotBlank
    LocalDateTime end;

    @NotBlank
    Integer itemId;
}
