package ru.practicum.shareit.review.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode(exclude = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    Integer id;

    String message;

    User author;

    User owner;
}
