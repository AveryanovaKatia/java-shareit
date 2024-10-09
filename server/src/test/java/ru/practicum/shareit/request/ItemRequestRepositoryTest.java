package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserRepository userRepository;

    final Sort sort = Sort.by(Sort.Direction.DESC, "created");

    @Test
    @DisplayName("ItemRequestRepository_empty")
    public void testEmpty() {

        final List<ItemRequest> itemRequests = itemRequestRepository.findAll();

        assertTrue(itemRequests.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestRepository_findAllByRequestorId")
    public void testFindAllByRequestorId() {

        final User user = new User();
        user.setName("Katia");
        user.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user);

        final ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("coffee machine");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        final List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(1, sort);

        assertEquals(1, itemRequestList.size());
        assertEquals("Katia", itemRequestList.getFirst().getRequestor().getName());
        assertEquals("coffee machine", itemRequestList.getFirst().getDescription());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestRepository_findAllByRequestorIdNot")
    public void testFindAllByRequestorIdNot() {
        final User user = new User();
        user.setName("Katia");
        user.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user);

        final User user1 = new User();
        user1.setName("Olia");
        user1.setEmail("molnia@yandex.ru");
        userRepository.save(user1);

        final ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("coffee machine");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        final ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("coffee grinder");
        itemRequest1.setRequestor(user1);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest1);

        final ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("coffee maker");
        itemRequest2.setRequestor(user1);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest2);

        final List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdNot(1, sort);

        assertEquals(2, itemRequestList.size());
    }
}
