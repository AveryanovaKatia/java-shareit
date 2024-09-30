package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponceDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    static final String HEADER = "X-Sharer-User-Id";

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("ItemRequestController_saveItemRequest")
    void saveItemRequestTest() throws Exception {

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Bla-bla-bla");

        ItemRequestResponceDto responceDto = new ItemRequestResponceDto();
        responceDto.setId(1);
        responceDto.setDescription("Bla-bla-bla");
        responceDto.setCreated(LocalDateTime.now());

        when(itemRequestService.saveItemRequest(anyInt(), any(ItemRequestDto.class))).thenReturn(responceDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"description\": \"Bla-bla-bla\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Bla-bla-bla"));

        verify(itemRequestService, times(1))
                .saveItemRequest(anyInt(), any(ItemRequestDto.class));
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("ItemRequestController_getAllByUser")
    void getAllByUserTest() throws Exception {
        List<ItemRequestResponceDto> requests = List.of(new ItemRequestResponceDto()); // создайте список

        when(itemRequestService.getAllByUser(anyInt())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService, times(1)).getAllByUser(anyInt());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("ItemRequestController_getAll")
    void getAllTest() throws Exception {
        List<ItemRequestResponceDto> responceDtos = List.of(new ItemRequestResponceDto());

        when(itemRequestService.getAll(anyInt())).thenReturn(responceDtos);

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService, times(1)).getAll(anyInt());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("ItemRequestController_getById")
    void getByIdTest() throws Exception {
        ItemRequestResponceDto request = new ItemRequestResponceDto(); // создайте DTO

        when(itemRequestService.getById(anyInt())).thenReturn(request);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()));

        verify(itemRequestService, times(1)).getById(anyInt());
    }
}