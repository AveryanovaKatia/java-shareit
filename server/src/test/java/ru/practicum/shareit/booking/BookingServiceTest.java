package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponce;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceTest {

    BookingService bookingService;

    BookingMapper bookingMapper;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    User user1;

    Item item1;

    Item item2;

    BookingRequest bookingRequest1;

    Booking booking;

    Booking booking1;

    @BeforeEach
    public void setUp() {

        bookingMapper = new BookingMapper();

        UserMapper userMapper = new UserMapper();

        ItemMapper itemMapper = new ItemMapper();

        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper,
                userRepository, userMapper, itemRepository, itemMapper);

        user1 = new User();
        user1.setName("Mia");
        user1.setEmail("midnight@yandex.ru");
        user1.setId(1);

        item1 = new Item();
        item1.setName("Table");
        item1.setDescription("oak");
        item1.setOwner(user1);
        item1.setAvailable(true);

        item2 = new Item();
        item2.setName("Vase");
        item2.setDescription("2 litres");
        item2.setOwner(user1);
        item2.setAvailable(false);

        bookingRequest1 = new BookingRequest();
        bookingRequest1.setItemId(1);
        bookingRequest1.setStart(LocalDateTime.now().plusDays(2));
        bookingRequest1.setEnd(LocalDateTime.now().plusDays(5));

        booking = new Booking();
        booking.setItem(item1);
        booking.setBooker(user1);
        booking.setId(1);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(5));

        booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setId(2);
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(5));
    }

    @Test
    @DisplayName("BookingService_createNotUser")
    void testCreateNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.saveRequest(bookingRequest1, 1)
        );
    }

    @Test
    @DisplayName("BookingService_createNotItem")
    void testCreateNotItem() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        assertThrows(
                NotFoundException.class,
                () -> bookingService.saveRequest(bookingRequest1, 1)
        );
    }

    @Test
    @DisplayName("BookingService_createNotAvailable")
    void testCreateNotAvailable() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item2));

        assertThrows(
                ValidationException.class,
                () -> bookingService.saveRequest(bookingRequest1, 1)
        );
    }

    @Test
    @DisplayName("BookingService_createNotGoodTime")
    void testCreateNotGoodTime() {

        final BookingRequest bookingRequest2;
        bookingRequest2 = new BookingRequest();
        bookingRequest2.setItemId(1);
        bookingRequest2.setStart(LocalDateTime.now().plusDays(7));
        bookingRequest2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.saveRequest(bookingRequest2, 1)
        );
    }

    @Test
    @DisplayName("BookingService_createEqualsTime")
    void testCreateEqualsTime() {

        final LocalDateTime time = LocalDateTime.now().plusDays(5);

        final BookingRequest bookingRequest2;
        bookingRequest2 = new BookingRequest();
        bookingRequest2.setItemId(1);
        bookingRequest2.setStart(time);
        bookingRequest2.setEnd(time);

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.saveRequest(bookingRequest2, 1)
        );
    }

    @Test
    @DisplayName("BookingService_create")
    void testCreate() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(bookingRepository.save(bookingMapper.toBooking(bookingRequest1, user1, item1))).thenReturn(booking);

        final BookingResponce bookingResponce = bookingService.saveRequest(bookingRequest1, 1);

        assertEquals("Mia", bookingResponce.getBooker().getName());
        assertEquals("Table", bookingResponce.getItem().getName());
        assertEquals(Status.WAITING, bookingResponce.getStatus());
    }

    @Test
    @DisplayName("BookingService_approvedNotBooking")
    void testApprovedNotBooking() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.approved(1, 1, true)
        );
    }

    @Test
    @DisplayName("BookingService_approvedUserNotOwner")
    void testApprovedUserNotOwner() {

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        assertThrows(
                ValidationException.class,
                () -> bookingService.approved(2, 1, true)
        );
    }

    @Test
    @DisplayName("BookingService_approvedStatusNotWaiting")
    void testApprovedStatusNotWaiting() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(bookingRepository.findById(2)).thenReturn(Optional.of(booking2));

        assertThrows(
                ValidationException.class,
                () -> bookingService.approved(1, 2, true)
        );
    }

    @Test
    @DisplayName("BookingService_approvedTrue")
    void testApprovedTrue() {

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        final BookingResponce bookingResponce = bookingService.approved(1, 1, true);

        assertEquals(Status.APPROVED, bookingResponce.getStatus());
        assertEquals("Mia", bookingResponce.getBooker().getName());
        assertEquals("Table", bookingResponce.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_approvedFalse")
    void testApprovedFalse() {

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        final BookingResponce bookingResponce = bookingService.approved(1, 1, false);

        assertEquals(Status.REJECTED, bookingResponce.getStatus());
        assertEquals("Mia", bookingResponce.getBooker().getName());
        assertEquals("Table", bookingResponce.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_findByIdNotBooking")
    void testFindByIdNotBooking() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(1, 1)
        );
    }

    @Test
    @DisplayName("BookingService_findByIdUserNotOwner")
    void testFindByIdUserNotOwner() {

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        assertThrows(
                ValidationException.class,
                () -> bookingService.findById(2, 1)
        );
    }

    @Test
    @DisplayName("BookingService_findById")
    void testFindById() {

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        final BookingResponce bookingResponce = bookingService.findById(1, 1);

        assertEquals("Mia", bookingResponce.getBooker().getName());
        assertEquals("Table", bookingResponce.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdNotUser")
    void testFindAllByUserIdNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.findAllByUserId(1, "all")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdAll")
    void testFindAllByUserIdAll() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerId(1, sort)).thenReturn(List.of(booking, booking1));

        final List<BookingResponce> bookingResponces = bookingService.findAllByUserId(1, "all");

        assertEquals(2, bookingResponces.size());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdCurrent")
    void testFindAllByUserIdCurrent() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyInt(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking2));

        final List<BookingResponce> bookingResponces = bookingService.findAllByUserId(1, "CURRENT");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookingResponces.getFirst().getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdPast")
    void testFindAllByUserIdPast() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyInt(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking1));

        final List<BookingResponce> bookingResponces = bookingService.findAllByUserId(1, "PAST");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdFuture")
    void testFindAllByUserIdFuture() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndStartAfter(anyInt(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        final List<BookingResponce> bookingResponces = bookingService.findAllByUserId(1, "FUTURE");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdWaiting")
    void testFindAllByUserIdWaiting() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndStatusIs(1, Status.WAITING, sort))
                .thenReturn(List.of(booking));

        final List<BookingResponce> bookingResponces = bookingService.findAllByUserId(1, "WAITING");

        assertEquals(1, bookingResponces.size());
        assertEquals(Status.WAITING, bookingResponces.getFirst().getStatus());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdRejected")
    void testFindAllByUserIdRejected() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBookerIdAndStatusIs(1, Status.REJECTED, sort))
                .thenReturn(List.of(booking2));

        final List<BookingResponce> bookingResponces = bookingService.findAllByUserId(1, "REJECTED");

        assertEquals(1, bookingResponces.size());
        assertEquals(Status.REJECTED, bookingResponces.getFirst().getStatus());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdValid")
    void testFindAllByUserIdValid() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByUserId(1, "bla")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdNotUser")
    void testFindAllByOwnerIdNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.findAllByOwnerId(1, "all")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdNotItem")
    void testFindAllByOwnerIdNotItem() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByOwnerId(1, "all")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdAll")
    void testFindAllByOwnerIdAll() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerId(1, sort)).thenReturn(List.of(booking, booking1));

        final List<BookingResponce> bookingResponces = bookingService.findAllByOwnerId(1, "all");

        assertEquals(2, bookingResponces.size());
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdCurrent")
    void testFindAllByOwnerIdCurrent() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyInt(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking2));

        final List<BookingResponce> bookingResponces = bookingService.findAllByOwnerId(1, "CURRENT");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookingResponces.getFirst().getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdPast")
    void testFindAllByOwnerIdPast() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyInt(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking1));

        final List<BookingResponce> bookingResponces = bookingService.findAllByOwnerId(1, "PAST");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdFuture")
    void testFindAllByOwnerIdFuture() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyInt(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(booking));

        final List<BookingResponce> bookingResponces = bookingService.findAllByOwnerId(1, "FUTURE");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdWaiting")
    void testFindAllByOwnerIdWaiting() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(1, Status.WAITING, sort))
                .thenReturn(List.of(booking));

        final List<BookingResponce> bookingResponces = bookingService.findAllByOwnerId(1, "WAITING");

        assertEquals(1, bookingResponces.size());
        assertEquals(Status.WAITING, bookingResponces.getFirst().getStatus());
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdRejected")
    void testFindAllByOwnerIdRejected() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(1, Status.REJECTED, sort))
                .thenReturn(List.of(booking2));

        final List<BookingResponce> bookingResponces = bookingService.findAllByOwnerId(1, "REJECTED");

        assertEquals(1, bookingResponces.size());
        assertEquals(Status.REJECTED, bookingResponces.getFirst().getStatus());
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdValid")
    void testFindAllByOwnerIdValid() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1, item2));

        assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByOwnerId(1, "bla")
        );
    }
}
