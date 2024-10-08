package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponce;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceSBTest {

    BookingService bookingService;

    ItemService itemService;

    UserService userService;

    @BeforeEach
    public void setUp() {

        final UserDto userDto1 = new UserDto();
        userDto1.setName("Katia");
        userDto1.setEmail("gromgrommolnia@mail.ru");
        userService.save(userDto1);

        final UserDto userDto2 = new UserDto();
        userDto2.setName("Nika");
        userDto2.setEmail("moemore@mail.ru");
        userService.save(userDto2);

        final UserDto userDto3 = new UserDto();
        userDto3.setName("Mia");
        userDto3.setEmail("midnight@mail.ru");
        userService.save(userDto3);

        final ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Vase");
        itemDto1.setDescription("2 litres");
        itemDto1.setAvailable(true);
        itemService.save(1, itemDto1);

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Table");
        itemDto2.setDescription("oak");
        itemDto2.setAvailable(true);
        itemService.save(1, itemDto2);

        final ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("Spoons");
        itemDto3.setDescription("Silver");
        itemDto3.setAvailable(true);
        itemService.save(2, itemDto3);

        final BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setItemId(2);
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(3));
        bookingService.saveRequest(bookingRequest,2);

        final BookingRequest bookingRequest2 = new BookingRequest();
        bookingRequest2.setItemId(3);
        bookingRequest2.setStart(LocalDateTime.now().plusDays(10));
        bookingRequest2.setEnd(LocalDateTime.now().plusDays(13));
        bookingService.saveRequest(bookingRequest2,2);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("BookingService_saveRequest")
    void testSaveRequest() {

        final BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setItemId(1);
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(3));

        final BookingResponce bookingResponce = bookingService.saveRequest(bookingRequest, 1);

        assertEquals("Vase", bookingResponce.getItem().getName());
        assertEquals("Katia", bookingResponce.getBooker().getName());
        assertEquals(Status.WAITING, bookingResponce.getStatus());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("BookingService_approved")
    void testApproved() {

        final BookingResponce bookingResponce = bookingService.approved(1, 1, true);

        assertEquals(Status.APPROVED, bookingResponce.getStatus());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("BookingService_findById")
    void testFindById() {

        final BookingResponce bookingResponce1 = bookingService.findById(1, 1);

        final BookingResponce bookingResponce2 = bookingService.findById(2, 1);

        assertEquals(bookingResponce1, bookingResponce2);
        assertEquals("Table", bookingResponce1.getItem().getName());
        assertEquals("Table", bookingResponce2.getItem().getName());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("BookingService_findAllByUserId")
    void testFindAllByUserId() {

        final List<BookingResponce> bookingResponces = bookingService.findAllByUserId(2, "all");

        assertEquals(2, bookingResponces.size());
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("BookingService_findAllByOwnerId")
    void testFindAllByOwnerId() {

        final List<BookingResponce> bookingResponces = bookingService.findAllByOwnerId(1, "all");

        assertEquals(1, bookingResponces.size());
    }
}
