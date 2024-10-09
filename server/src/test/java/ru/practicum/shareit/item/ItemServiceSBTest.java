package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceSBTest {

    ItemService itemService;

    UserService userService;

    BookingService bookingService;

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

        final BookingRequest bookingRequest1 = new BookingRequest();
        bookingRequest1.setItemId(1);
        bookingRequest1.setStart(LocalDateTime.now().minusDays(6));
        bookingRequest1.setEnd(LocalDateTime.now().minusDays(2));
        bookingService.saveRequest(bookingRequest1, 2);
        bookingService.approved(1, 1, true);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("ItemService_update")
    void testUpdate() {

        final ItemDto itemDto = new ItemDto();
        itemDto.setName("Oil painting");
        itemDto.setDescription("Dawn by the water");
        itemDto.setAvailable(true);
        itemService.save(1, itemDto);

        final ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Painting");
        itemDto1.setDescription("Dawn");
        itemDto1.setAvailable(true);
        itemService.update(1, 4, itemDto1);

        assertEquals("Painting", itemService.findById(1, 4).getName());
        assertEquals("Dawn", itemService.findById(1, 4).getDescription());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("ItemService_getItemsByOwnerId")
    void testGetItemsByOwnerId() {

        assertEquals(2, itemService.getItemsByOwnerId(1).size());
        assertEquals(1, itemService.getItemsByOwnerId(2).size());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("ItemService_search")
    void testSearch() {

        assertEquals(1, itemService.search("Table").size());
        assertEquals(2, itemService.search("S").size());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("ItemService_saveComment")
    void testSaveComment() {

        final CommentDto commentDto1 = new CommentDto();
        commentDto1.setText("Bla-bla-bla");

        final CommentDto commentDto2 = itemService.saveComment(2, 1, commentDto1);

        assertEquals("Bla-bla-bla", commentDto2.getText());
        assertEquals("Nika", commentDto2.getAuthorName());
    }


}