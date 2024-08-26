package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ImportResource
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceTest {

    ItemService itemService;

    UserService userService;

    @BeforeEach
    public void createUsersAndItems() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("Katia");
        userDto1.setEmail("gromgrommolnia@mail.ru");
        userService.create(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("Nika");
        userDto2.setEmail("moemore@mail.ru");
        userService.create(userDto2);

        UserDto userDto3 = new UserDto();
        userDto3.setName("Mia");
        userDto3.setEmail("midnight@mail.ru");
        userService.create(userDto3);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Vase");
        itemDto1.setDescription("2 litres");
        itemDto1.setAvailable(true);
        itemService.create(1, itemDto1);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Table");
        itemDto2.setDescription("oak");
        itemDto2.setAvailable(true);
        itemService.create(1, itemDto2);

        ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("Spoons");
        itemDto3.setDescription("Silver");
        itemDto3.setAvailable(true);
        itemService.create(2, itemDto3);
    }

    @AfterEach
    public void deleteUsers() {
        Integer countUser = 1;
        while(!userService.findAll().isEmpty()) {
            userService.delete(countUser);
            countUser++;
        }
        itemService.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("ItemService_getOwnerItems")
    void getOwnerItemsTest() {
        assertEquals(2, itemService.getOwnerItems(1).size());
        assertEquals(1, itemService.getOwnerItems(2).size());
    }

    @Test
    @Order(2)
    @DisplayName("ItemService_update")
    void updateTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Oil painting");
        itemDto.setDescription("Dawn by the water");
        itemDto.setAvailable(true);
        itemService.create(1, itemDto);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Painting");
        itemDto1.setDescription("Dawn");
        itemDto1.setAvailable(true);
        itemService.update(1, 4, itemDto1);

        assertEquals("Painting", itemService.getById(4).getName());
        assertEquals("Dawn", itemService.getById(4).getDescription());
    }

    @Test
    @Order(3)
    @DisplayName("ItemService_search")
    void searchTest() {
        assertEquals(1, itemService.search("Table").size());
        assertEquals(2, itemService.search("S").size());
    }
}
