package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@EqualsAndHashCode(exclude = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {

    Integer id;

    String description;

    User requestor;

    LocalDate created;
}
