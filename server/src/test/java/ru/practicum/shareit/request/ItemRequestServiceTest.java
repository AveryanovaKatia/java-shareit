package ru.practicum.shareit.request;

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
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponceForIR;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponceDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ImportResource
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceTest {

    ItemRequestService itemRequestService;

    ItemService itemService;

    UserService userService;

    @BeforeEach
    public void createUsersAndItems() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("Katia");
        userDto1.setEmail("gromgrommolnia@mail.ru");
        userService.save(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("Nika");
        userDto2.setEmail("moemore@mail.ru");
        userService.save(userDto2);

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("coffee machine");
        itemRequestService.saveItemRequest(1, itemRequestDto1);

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("coffee grinder");
        itemRequestService.saveItemRequest(1, itemRequestDto2);

        ItemRequestDto itemRequestDto3 = new ItemRequestDto();
        itemRequestDto3.setDescription("coffee maker");
        itemRequestService.saveItemRequest(2, itemRequestDto3);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("coffee maker");
        itemDto.setDescription("2 litres");
        itemDto.setAvailable(true);
        itemDto.setRequestId(3);
        itemService.save(1, itemDto);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("ItemRequestService_saveItemRequest")
    void saveItemRequestTest() {

        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Bla-bla-bla");

        final ItemRequestResponceDto i = itemRequestService.saveItemRequest(1, itemRequestDto);

        assertEquals("Katia", i.getRequestor().getName());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("ItemRequestService_getAllByUser")
    void getAllByUserTest() {

        final List<ItemRequestResponceDto> i = itemRequestService.getAllByUser(1);

        assertEquals(2, i.size());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("ItemRequestService_getAll")
    void getAllTest() {

        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Bla-bla-bla");
        itemRequestService.saveItemRequest(2, itemRequestDto);

        final List<ItemRequestResponceDto> i = itemRequestService.getAll(1);

        assertEquals(2, i.size());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("ItemRequestService_getById")
    void getByIdTest() {

        final ItemRequestResponceDto i = itemRequestService.getById(3);

        final List<ItemDtoResponceForIR> items = i.getItems();

        assertEquals("coffee maker", i.getDescription());
        assertEquals(1, items.size());
        assertEquals("coffee maker", items.get(0).getName());
    }
}
