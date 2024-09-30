package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingResponce;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponce saveRequest(@RequestHeader(HEADER) final Integer userId,
                                       @RequestBody final BookingRequest bookingRequest) {
        return bookingService.saveRequest(bookingRequest, userId);

    }

    @PatchMapping("/{bookingId}")
    public BookingResponce approved(@RequestHeader(HEADER) final Integer ownerId,
                                    @PathVariable final Integer bookingId,
                                    @RequestParam final boolean approved) {
        return bookingService.approved(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponce findById(@RequestHeader(HEADER) final Integer userId,
                                    @PathVariable final Integer bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponce> findAllByUserId(@RequestHeader(HEADER) final Integer userId,
                                                 @RequestParam(defaultValue = "all") final String state) {
        return bookingService.findAllByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponce> findAllByOwnerId(@RequestHeader(HEADER) final Integer ownerId,
                                                  @RequestParam(defaultValue = "all") final String state) {
        return bookingService.findAllByOwnerId(ownerId, state);
    }

}
