package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("UserController_save")
    void saveTest() throws Exception {
        final UserDto userDto = new UserDto();
        when(userService.save(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lena\", \"email\": \"gromgrommolnia@mail.ru\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService, times(1)).save(any(UserDto.class));
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("UserController_update")
    void updateTest() throws Exception {
        final UserDto userDto = new UserDto();
        when(userService.update(anyInt(), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Katia\", \"email\": \"grom@mail.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService, times(1)).update(anyInt(), any(UserDto.class));
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("UserController_findById")
    void findByIdTest() throws Exception {
        final UserDto userDto = new UserDto();
        when(userService.findById(anyInt())).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService, times(1)).findById(anyInt());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("UserController_findAll")
    void findAllTest() throws Exception {
        List<UserDto> users = List.of(new UserDto());
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(userService, times(1)).findAll();
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("UserController_delete")
    void deleteTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(anyInt());
    }
}