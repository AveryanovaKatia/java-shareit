package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponce;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    static final String HEADER = "X-Sharer-User-Id";

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("ItemController_save")
    void saveTest() throws Exception {
        final ItemDto itemDto = new ItemDto();
        when(itemService.save(anyInt(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"name\": \"coffee machine\", \"description\": \"Italian\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).save(anyInt(), any(ItemDto.class));
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("ItemController_update")
    void updateTest() throws Exception {
        final ItemDto itemDto = new ItemDto();
        when(itemService.update(anyInt(), anyInt(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .content("{\"name\": \"Updated item\", \"description\": \"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).update(anyInt(), anyInt(), any(ItemDto.class));
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("ItemController_findById")
    void findByIdTest() throws Exception {
        ItemResponce itemResponce = new ItemResponce();
        when(itemService.findById(anyInt(), anyInt())).thenReturn(itemResponce);

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponce.getId()));

        verify(itemService, times(1)).findById(anyInt(), anyInt());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("ItemController_getItemsByOwnerId")
    void getItemsByOwnerIdTest() throws Exception {
        List<ItemDto> items = List.of(new ItemDto());
        when(itemService.getItemsByOwnerId(anyInt())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemService, times(1)).getItemsByOwnerId(anyInt());
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("ItemController_search")
    void searchTest() throws Exception {
        List<ItemDto> items = List.of(new ItemDto());
        when(itemService.search(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemService, times(1)).search(anyString());
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("ItemController_saveComment")
    void saveCommentTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment-bla-bla");

        when(itemService.saveComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"text\": \"Comment-bla-bla\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Comment-bla-bla"));

        verify(itemService, times(1)).saveComment(anyInt(), anyInt(), any(CommentDto.class));
    }
}

