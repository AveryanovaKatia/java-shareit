package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemResponce {

    Integer id;

    String name;

    String description;

    Boolean available;

    String ownerName;

    LocalDateTime lastBooking;

    LocalDateTime nextBooking;

    List<CommentDto> comments = new ArrayList<>();
}

