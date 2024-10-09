package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Test
    @DisplayName("BookingRepository_empty")
    public void testEmpty() {

        final List<Booking> bookings = bookingRepository.findAll();

        assertTrue(bookings.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByBookerId")
    public void testFindAllByBookerId() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.now().plusDays(2));
        booking1.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking1);

        final List<Booking> bookings = bookingRepository.findAllByBookerId(2, sort);

        assertEquals(1, bookings.getFirst().getId());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByBookerIdAndEndBeforeAndStartAfter")
    public void testFindAllByBookerIdAndEndBeforeAndStartAfter() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(2,
                LocalDateTime.now(), LocalDateTime.now(), sort);

        assertEquals(1, bookings.size());
        assertEquals("Vase", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByBookerIdAndEndBefore")
    public void testFindAllByBookerIdAndEndBefore() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBefore(2,
                LocalDateTime.now(), sort);

        assertEquals(1, bookings.size());
        assertEquals("Vase", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByBookerIdAndStartAfter")
    public void testFindAllByBookerIdAndStartAfter() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfter(2,
                LocalDateTime.now(), sort);

        assertEquals(1, bookings.size());
        assertEquals("Table", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByBookerIdAndStatusIsWaiting")
    public void testFindAllByBookerIdAndStatusIsWaiting() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusIs(2, Status.WAITING, sort);

        assertEquals(1, bookings.size());
        assertEquals("Table", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByBookerIdAndStatusIsRejected")
    public void testFindAllByBookerIdAndStatusIsRejected() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusIs(2, Status.REJECTED, sort);

        assertEquals(1, bookings.size());
        assertEquals("Table", bookings.getFirst().getItem().getName());
    }

    ////////////////


    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByItemOwnerId")
    public void testFindAllByItemOwnerId() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.now().plusDays(2));
        booking1.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking1);

        final List<Booking> bookings = bookingRepository.findAllByItemOwnerId(1, sort);

        assertEquals(1, bookings.getFirst().getId());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByItemOwnerIdAndEndBeforeAndStartAfter")
    public void testFindAllByItemOwnerIdAndEndBeforeAndStartAfter() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(1,
                LocalDateTime.now(), LocalDateTime.now(), sort);

        assertEquals(1, bookings.size());
        assertEquals("Vase", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByItemOwnerIdAndEndBefore")
    public void testFindAllByItemOwnerIdAndEndBefore() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(1,
                LocalDateTime.now(), sort);

        assertEquals(1, bookings.size());
        assertEquals("Vase", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByItemOwnerIdAndStartAfter")
    public void testFindAllByItemOwnerIdAndStartAfter() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(1,
                LocalDateTime.now(), sort);

        assertEquals(1, bookings.size());
        assertEquals("Table", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByItemOwnerIdAndStatusIsWaiting")
    public void testFindAllByItemOwnerIdAndStatusIsWaiting() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusIs(1, Status.WAITING, sort);

        assertEquals(1, bookings.size());
        assertEquals("Table", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findAllByItemOwnerIdAndStatusIsRejected")
    public void testFindAllByItemOwnerIdAndStatusIsRejected() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusIs(1, Status.REJECTED, sort);

        assertEquals(1, bookings.size());
        assertEquals("Table", bookings.getFirst().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc")
    public void testFindTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final Booking booking3 = new Booking();
        booking3.setBooker(user2);
        booking3.setItem(item2);
        booking3.setStatus(Status.REJECTED);
        booking3.setStart(LocalDateTime.now().minusDays(15));
        booking3.setEnd(LocalDateTime.now().minusDays(12));
        bookingRepository.save(booking1);

        final List<Status> statuses = List.of(Status.APPROVED, Status.REJECTED);

        final Optional<Booking> bookingOp = bookingRepository
                .findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(1, LocalDateTime.now(), statuses);

        assertTrue(bookingOp.isPresent());
        assertEquals("Vase", bookingOp.get().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findTopByItemIdAndEndBeforeAndStatusInOrderByEndDescIsEmpty")
    public void testFindTopByItemIdAndEndBeforeAndStatusInOrderByEndDescIsEmpty() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final List<Status> statuses = List.of(Status.APPROVED, Status.REJECTED);

        final Optional<Booking> bookingOp = bookingRepository
                .findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(1, LocalDateTime.now(), statuses);

        assertTrue(bookingOp.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc")
    public void testFindTopByItemIdAndStartAfterAndStatusInOrderByStartAsc() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Booking booking1 = new Booking();
        booking1.setBooker(user2);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking1);

        final Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item1);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        bookingRepository.save(booking2);

        final Booking booking3 = new Booking();
        booking3.setBooker(user2);
        booking3.setItem(item1);
        booking3.setStatus(Status.REJECTED);
        booking3.setStart(LocalDateTime.now().plusDays(10));
        booking3.setEnd(LocalDateTime.now().plusDays(12));
        bookingRepository.save(booking1);

        final List<Status> statuses = List.of(Status.APPROVED, Status.REJECTED);

        final Optional<Booking> bookingOp = bookingRepository
                .findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(1, LocalDateTime.now(), statuses);

        assertTrue(bookingOp.isPresent());
        assertEquals("Vase", bookingOp.get().getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingRepository_findTopByItemIdAndStartAfterAndStatusInOrderByStartAscIsEmpty")
    public void testFindTopByItemIdAndStartAfterAndStatusInOrderByStartAscIsEmpty() {

        final User user1 = new User();
        user1.setName("Katia");
        user1.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Mia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);
        userRepository.save(user2);

        final Item item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Table");
        item2.setDescription("oak");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final List<Status> statuses = List.of(Status.APPROVED, Status.REJECTED);

        final Optional<Booking> bookingOp = bookingRepository
                .findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(1, LocalDateTime.now(), statuses);

        assertTrue(bookingOp.isEmpty());
    }
}
