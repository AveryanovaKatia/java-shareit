package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceTest {

    UserService userService;

    UserMapper userMapper;

    @Mock
    UserRepository userRepository;

    UserDto userDto1;

    User user1;

    @BeforeEach
    public void setUp() {

        userMapper = new UserMapper();

        userService = new UserServiceImpl(userRepository, userMapper);

        userDto1 = new UserDto();
        userDto1.setName("Mia");
        userDto1.setEmail("midnight@mail.ru");

        user1 = new User();
        user1.setName("Mia");
        user1.setEmail("midnight@yandex.ru");
        user1.setId(1);
    }


    @Test
    @DisplayName("UserService_create")
    void testCreate() {

        when(userRepository.save(userMapper.toUser(userDto1))).thenReturn(user1);

        final UserDto userDto = userService.save(userDto1);

        assertEquals("Mia", userDto.getName());
        assertEquals(1, userDto.getId());
    }

    @Test
    @DisplayName("UserService_findById")
    void testFindById() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        assertEquals("Mia", userService.findById(1).getName());
    }

    @Test
    @DisplayName("UserService_findByIdNotUser")
    void testFindByIdNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> userService.findById(3)
        );
    }

    @Test
    @DisplayName("UserService_updateNotUser")
    void testUpdateNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> userService.update(1, userDto1)
        );
    }


    @Test
    @DisplayName("UserService_updateSetName")
    void testUpdateSetName() {

        final UserDto userDto2 = new UserDto();
        userDto2.setName("NeMia");

        final User user2 = new User();
        user2.setName("NeMia");
        user2.setEmail("midnight@yandex.ru");
        user2.setId(1);

        when(userRepository.save(userMapper.toUser(userDto1))).thenReturn(user1);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(userRepository.save(user2)).thenReturn(user2);

        userService.save(userDto1);

        final UserDto userDto = userService.update(1, userDto2);

        assertEquals("NeMia", userDto.getName());
    }

    @Test
    @DisplayName("UserService_updateSetEmail")
    void testUpdateSetEmail() {

        final UserDto userDto3 = new UserDto();
        userDto3.setEmail("neMidnight@mail.ru");

        final User user3 = new User();
        user3.setName("Mia");
        user3.setEmail("neMidnight@mail.ru");
        user3.setId(1);

        when(userRepository.save(userMapper.toUser(userDto1))).thenReturn(user1);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(userRepository.save(user3)).thenReturn(user3);

        userService.save(userDto1);

        final UserDto userDto = userService.update(1, userDto3);

        assertEquals("neMidnight@mail.ru", userDto.getEmail());
    }

    @Test
    @DisplayName("UserService_findAll")
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(user1));

        final List<UserDto> users = userService.findAll();

        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("UserService_deleteNotUser")
    void testDeleteNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> userService.delete(3)
        );
    }

    @Test
    @DisplayName("UserService_delete")
    void testDelete() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        userService.delete(1);

        verify(userRepository).delete(any(User.class));
    }
}
