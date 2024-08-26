package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ImportResource
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceTest {

    UserService userService;

    @BeforeEach
    public void createUsers() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("Katia");
        userDto1.setEmail("gromgrommolnia@mail.ru");
        userService.create(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("Nika");
        userDto2.setEmail("moemore@mail.ru");
        userService.create(userDto2);

        UserDto userDto = new UserDto();
        userDto.setName("Mia");
        userDto.setEmail("midnight@mail.ru");
        userService.create(userDto);
    }

    @AfterEach
    public void deleteUsers() {
        Integer count = 1;
        while (!userService.findAll().isEmpty()) {
            userService.delete(count);
            count++;
        }
    }

    @Test
    @Order(1)
    @DisplayName("UserService_create")
    void createTest() {
        assertEquals(3, userService.findAll().size());
    }

    @Test
    @Order(2)
    @DisplayName("UserService_update")
    void updateTest() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("NeMia");
        userDto1.setEmail("neMidnight@mail.ru");
        userService.update(3, userDto1);

        assertEquals("NeMia", userService.getById(3).getName());
        assertEquals("neMidnight@mail.ru", userService.getById(3).getEmail());

        UserDto userDto2 = new UserDto();
        userDto2.setName("Ania");
        userService.update(3, userDto2);

        assertEquals("Ania", userService.getById(3).getName());

        UserDto userDto3 = new UserDto();
        userDto3.setEmail("night@mail.ru");
        userService.update(3, userDto3);

        assertEquals("night@mail.ru", userService.getById(3).getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("UserService_getById")
    void getByIdTest() {
        assertEquals("Katia", userService.getById(1).getName());
    }

    @Test
    @Order(4)
    @DisplayName("UserService_delete")
    void deleteTest() {
        userService.delete(3);

        assertEquals(2, userService.findAll().size());
    }
}
