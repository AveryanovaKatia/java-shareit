package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Component
public class BookingMapper {

    public Booking toBooking(final BookingRequest bookingRequest, final User user, final Item item) {

        final Booking booking = new Booking();

        booking.setStart(bookingRequest.getStart());
        booking.setEnd(bookingRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        return booking;
    }

    public BookingResponce toBookingResponce(final Booking booking, final UserDto userDto,
                                             final ItemDto itemDto) {

        final BookingResponce bookingResponce = new BookingResponce();

        bookingResponce.setId(booking.getId());
        bookingResponce.setStart(booking.getStart());
        bookingResponce.setEnd(booking.getEnd());
        bookingResponce.setItem(itemDto);
        bookingResponce.setBooker(userDto);
        bookingResponce.setStatus(booking.getStatus());

        return bookingResponce;
    }
}
