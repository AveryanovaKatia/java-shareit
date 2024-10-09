package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceTest {

    ItemService itemService;

    ItemMapper itemMapper;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    User user1;

    ItemDto itemDto1;

    Item item1;

    Item item2;

    CommentDto commentDto1;

    Comment comment;

    @BeforeEach
    public void setUp() {

        itemMapper = new ItemMapper();

        CommentMapper commentMapper = new CommentMapper();

        itemService = new ItemServiceImpl(itemRepository, itemMapper, userRepository, commentRepository,
                commentMapper, bookingRepository, itemRequestRepository);


        user1 = new User();
        user1.setName("Mia");
        user1.setEmail("midnight@yandex.ru");
        user1.setId(1);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("vase big");
        itemRequest1.setRequestor(user1);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setId(1);

        itemDto1 = new ItemDto();
        itemDto1.setName("Vase");
        itemDto1.setDescription("2 litres");
        itemDto1.setAvailable(true);


        item1 = new Item();
        item1.setName("Vase");
        item1.setDescription("2 litres");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setId(1);

        item2 = new Item();
        item2.setName("Vase");
        item2.setDescription("2 litres");
        item2.setOwner(user1);
        item2.setAvailable(true);
        item2.setRequest(itemRequest1);

        commentDto1 = new CommentDto();
        commentDto1.setText("Bla-bla-bla");
        commentDto1.setAuthorName("Mia");
        commentDto1.setCreated(LocalDateTime.now());
        commentDto1.setId(1);

        comment = new Comment();
        comment.setId(1);
        comment.setItem(item1);
        comment.setText("Bla-bla-bla");
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    @DisplayName("ItemService_createNotOwner")
    void testCreateNotOwner() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.save(1, itemDto1)
        );
    }

    @Test
    @DisplayName("ItemService_createWithOutRequest")
    void testCreateWithOutRequest() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.save(itemMapper.toItem(user1, itemDto1))).thenReturn(item1);

        final ItemDto itemDto = itemService.save(1, itemDto1);

        assertEquals("Vase", itemDto.getName());
    }

    @Test
    @DisplayName("ItemService_createWithRequest")
    void testCreateWithRequest() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.save(itemMapper.toItem(user1, itemDto1))).thenReturn(item2);

        final ItemDto itemDto = itemService.save(1, itemDto1);

        assertEquals("Vase", itemDto.getName());
        assertEquals(1, itemDto.getRequestId());
    }

    @Test
    @DisplayName("ItemService_createWithRequestEmpty")
    void testCreateWithRequestEmpty() {

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Vase");
        itemDto2.setDescription("2 litres");
        itemDto2.setAvailable(true);
        itemDto2.setRequestId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.save(itemMapper.toItem(user1, itemDto2))).thenReturn(item2);

        assertThrows(
                NotFoundException.class,
                () -> itemService.save(1, itemDto2)
        );
    }

    @Test
    @DisplayName("ItemService_updateNotItem")
    void testUpdateNotItem() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.update(1, 1, itemDto1)
        );
    }

    @Test
    @DisplayName("ItemService_updateNotEqualsOwner")
    void testUpdateNotEqualsOwner() {

        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        assertThrows(
                ForbiddenException.class,
                () -> itemService.update(2, 1, itemDto1)
        );
    }

    @Test
    @DisplayName("ItemService_updateName")
    void testUpdateName() {

        final Item item11 = new Item();
        item11.setName("Glass vase");
        item11.setDescription("2 litres");
        item11.setOwner(user1);
        item11.setAvailable(true);

        final ItemDto itemDto11 = new ItemDto();
        itemDto11.setName("Glass vase");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(itemRepository.save(item11)).thenReturn(item11);

        final ItemDto itemDto = itemService.update(1, 1, itemDto11);

        assertEquals("Glass vase", itemDto.getName());
    }

    @Test
    @DisplayName("ItemService_updateDescription")
    void testUpdateDescription() {

        final Item item12 = new Item();
        item12.setName("Vase");
        item12.setDescription("3 litres");
        item12.setOwner(user1);
        item12.setAvailable(true);

        final ItemDto itemDto12 = new ItemDto();
        itemDto12.setDescription("3 litres");

        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(itemRepository.save(item12)).thenReturn(item12);

        final ItemDto itemDto = itemService.update(1, 1, itemDto12);

        assertEquals("3 litres", itemDto.getDescription());
    }

    @Test
    @DisplayName("ItemService_updateAvailable")
    void testUpdateAvailable() {

        final Item item13 = new Item();
        item13.setName("Vase");
        item13.setDescription("2 litres");
        item13.setOwner(user1);
        item13.setAvailable(false);

        final ItemDto itemDto13 = new ItemDto();
        itemDto13.setAvailable(false);

        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(itemRepository.save(item13)).thenReturn(item13);

        final ItemDto itemDto = itemService.update(1, 1, itemDto13);

        assertEquals(false, itemDto.getAvailable());
    }

    @Test
    @DisplayName("ItemService_findByIdNotItem")
    void testFindByIdNotItem() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.findById(2, 5)
        );
    }

    @Test
    @DisplayName("ItemService_findByIdNoOwner")
    void testFindByIdNoOwner() {

        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1)).thenReturn(List.of(comment));

        itemService.findById(5, 1);

        verify(bookingRepository, never()).findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(1,
                LocalDateTime.now(), List.of(Status.APPROVED));
        verify(bookingRepository, never()).findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(1,
                LocalDateTime.now(), List.of(Status.APPROVED));
    }


    @Test
    @DisplayName("ItemService_deleteNotItem")
    void testDeleteNotItem() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.delete(5)
        );
    }

    @Test
    @DisplayName("ItemService_delete")
    void testDelete() {

        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        itemService.delete(1);

        verify(itemRepository).delete(any(Item.class));
    }

    @Test
    @DisplayName("ItemService_getItemsByOwnerId")
    void testGetItemsByOwnerId() {

        when(itemRepository.findAllByOwnerId(1)).thenReturn(List.of(item1));

        final List<ItemDto> items = itemService.getItemsByOwnerId(1);

        assertEquals(1, items.size());
    }

    @Test
    @DisplayName("ItemService_getEmptyItemsListByOwnerId")
    void testGetEmptyItemsListByOwnerId() {

        when(itemRepository.findAllByOwnerId(5)).thenReturn(List.of());

        final List<ItemDto> itemDtos = itemService.getItemsByOwnerId(5);

        assertTrue(itemDtos.isEmpty());
    }

    @Test
    @DisplayName("ItemService_searchTextEmpty")
    void testSearchTextEmpty() {

        final List<ItemDto> itemDtos = itemService.search("");

        assertTrue(itemDtos.isEmpty());
    }

    @Test
    @DisplayName("ItemService_searchText")
    void testSearchText() {

        when(itemRepository.search("as")).thenReturn(List.of(item1));

        final List<ItemDto> itemDtos = itemService.search("as");

        assertEquals("Vase", itemDtos.getFirst().getName());
    }

    @Test
    @DisplayName("ItemService_saveCommentNotUser")
    void testSaveCommentNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.saveComment(5, 1, commentDto1)
        );
    }


    @Test
    @DisplayName("ItemService_saveCommentNotItem")
    void testSaveCommentNotItem() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        assertThrows(
                NotFoundException.class,
                () -> itemService.saveComment(1, 5, commentDto1)
        );
    }

    @Test
    @DisplayName("ItemService_noSaveCommentWithOutBooking")
    void testNoSaveCommentWithOutBooking() {

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        assertThrows(
                ValidationException.class,
                () -> itemService.saveComment(1, 1, commentDto1)
        );

    }

    @Test
    @DisplayName("ItemService_saveComment")
    void testSaveComment() {

        final Booking booking = new Booking();
        booking.setItem(item1);
        booking.setBooker(user1);
        booking.setId(1);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(7));


        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyInt(),
                anyInt(), any(Status.class), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        final CommentDto commentDto = itemService.saveComment(1, 1, commentDto1);

        assertEquals("Bla-bla-bla", commentDto.getText());
    }
}
