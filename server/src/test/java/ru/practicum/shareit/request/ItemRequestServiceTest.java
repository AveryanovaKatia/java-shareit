package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponceForIR;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponceDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceTest {

    ItemRequestService itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemMapper itemMapper;

    final Sort sort = Sort.by(Sort.Direction.DESC, "created");

    User user1;

    ItemRequestDto itemRequestDto1;

    ItemRequest itemRequest1;

    ItemRequest itemRequest2;

    Item item1;

    Item item2;

    @BeforeEach
    public void setUp() {

        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

        UserMapper userMapper = new UserMapper();

        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRequestMapper, userRepository,
                userMapper, itemRepository, itemMapper);

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("La-la-la");

        user1 = new User();
        user1.setName("Mia");
        user1.setEmail("midnight@yandex.ru");
        user1.setId(1);

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setDescription("La-la-la");
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setRequestor(user1);

        itemRequest2 = new ItemRequest();
        itemRequest2.setId(2);
        itemRequest2.setDescription("Gla-gla-gla");
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setRequestor(user1);

        item1 = new Item();
        item1.setName("Table");
        item1.setDescription("oak");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setRequest(itemRequest1);

        item2 = new Item();
        item2.setName("Vase");
        item2.setDescription("2 litres");
        item2.setOwner(user1);
        item2.setAvailable(true);
        item2.setRequest(itemRequest2);

        ItemDtoResponceForIR i1 = new ItemDtoResponceForIR();
        i1.setItemId(1);
        i1.setOwnerId(1);
        i1.setItemId(1);

    }

    @Test
    @DisplayName("ItemRequestService_createNotUser")
    void testCreateNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.saveItemRequest(1, itemRequestDto1)
        );
    }

    @Test
    @DisplayName("ItemRequestService_create")
    void testCreate() {

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest1);

        final ItemRequestResponceDto itemRequestResponceDto =
                itemRequestService.saveItemRequest(1, itemRequestDto1);

        assertEquals("La-la-la", itemRequestResponceDto.getDescription());
        assertEquals("Mia", itemRequestResponceDto.getRequestor().getName());
    }

    @Test
    @DisplayName("ItemRequestService_getAllByUser")
    void testGetAllByUser() {

        when(itemRequestRepository.findAllByRequestorId(1, sort))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequest(itemRequest1)).thenReturn(List.of(item1));
        when(itemRepository.findAllByRequest(itemRequest2)).thenReturn(List.of(item2));

        final List<ItemRequestResponceDto> itemRequestResponceDtoList = itemRequestService.getAllByUser(1);

        assertEquals(2, itemRequestResponceDtoList.size());
        assertEquals("Mia", itemRequestResponceDtoList.getFirst().getRequestor().getName());
    }

    @Test
    @DisplayName("ItemRequestService_getAll")
    void testGetAll() {

        when(itemRequestRepository.findAllByRequestorIdNot(2, sort))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        final List<ItemRequestResponceDto> itemRequestResponceDtoList = itemRequestService.getAll(2);

        assertEquals(2, itemRequestResponceDtoList.size());
        assertEquals("Mia", itemRequestResponceDtoList.getFirst().getRequestor().getName());
    }

    @Test
    @DisplayName("ItemRequestService_getByIdNotRequest")
    void testGetByIdNotRequest() {

        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(1)
        );
    }

    @Test
    @DisplayName("ItemRequestService_getById")
    void testGetById() {

        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByRequest(itemRequest1)).thenReturn(List.of(item1));

        final ItemRequestResponceDto itemRequestResponceDto = itemRequestService.getById(1);

        assertEquals("La-la-la", itemRequestResponceDto.getDescription());
        assertEquals("Mia", itemRequestResponceDto.getRequestor().getName());
        assertEquals(1, itemRequestResponceDto.getItems().size());
    }
}
