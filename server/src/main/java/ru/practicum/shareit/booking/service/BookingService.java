package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponce;

import java.util.List;

public interface BookingService {

    BookingResponce saveRequest(final BookingRequest bookingRequest, final Integer userId);

    BookingResponce approved(final Integer ownerId, final Integer bookingId, final boolean approved);

    BookingResponce findById(final Integer userId, final Integer bookingId);

    List<BookingResponce> findAllByUserId(final Integer userId, final String state);

    List<BookingResponce> findAllByOwnerId(final Integer ownerId, final String state);
}
