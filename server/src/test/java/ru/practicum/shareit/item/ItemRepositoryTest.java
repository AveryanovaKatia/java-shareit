package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    @DisplayName("ItemRepository_empty")
    public void testEmpty() {

        final List<Item> items = itemRepository.findAll();

        assertTrue(items.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRepository_findAllByOwnerId")
    public void testFindAllByOwnerId() {

        final User user = new User();
        user.setName("Katia");
        user.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user);

        final Item item = new Item();
        item.setName("Vase");
        item.setDescription("2 litres");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        final List<Item> items = itemRepository.findAllByOwnerId(1);

        assertEquals(1, items.size());
        assertEquals("Katia", items.getFirst().getOwner().getName());
        assertEquals("Vase", items.getFirst().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRepository_search_ThereAreItems")
    public void testSearch_ThereAreItems() {

        final User user = new User();
        user.setName("Katia");
        user.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user);

        final User user1 = new User();
        user1.setName("Olia");
        user1.setEmail("molnia@yandex.ru");
        userRepository.save(user1);


        final Item item = new Item();
        item.setName("Vase");
        item.setDescription("2 litres");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        final Item item1 = new Item();
        item1.setName("Table");
        item1.setDescription("oak");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Spoons");
        item2.setDescription("Silver");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Item item3 = new Item();
        item3.setName("Vase Gold");
        item3.setDescription("3 litres");
        item3.setOwner(user1);
        item3.setAvailable(true);
        itemRepository.save(item3);

        List<Item> items = itemRepository.search("Vase");

        assertEquals(2, items.size());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRepository_search_ThereAreNotItems")
    public void testSearch_ThereAreNotItems() {

        final User user = new User();
        user.setName("Katia");
        user.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user);

        final User user1 = new User();
        user1.setName("Olia");
        user1.setEmail("molnia@yandex.ru");
        userRepository.save(user1);

        final Item item = new Item();
        item.setName("Vase");
        item.setDescription("2 litres");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        final Item item1 = new Item();
        item1.setName("Table");
        item1.setDescription("oak");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final List<Item> items = itemRepository.search("Sun");

        assertTrue(items.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRepository_findAllByRequest")
    public void testSearch_findAllByRequest() {

        final User user = new User();
        user.setName("Katia");
        user.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user);

        final User user1 = new User();
        user1.setName("Olia");
        user1.setEmail("molnia@yandex.ru");
        userRepository.save(user1);

        final ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Vase 2 litres");
        itemRequest.setRequestor(user1);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        final Item item = new Item();
        item.setName("Vase");
        item.setDescription("2 litres");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequest(itemRequest);
        itemRepository.save(item);

        final Item item1 = new Item();
        item1.setName("Table");
        item1.setDescription("oak");
        item1.setOwner(user1);
        item1.setAvailable(true);
        itemRepository.save(item1);

        final Item item2 = new Item();
        item2.setName("Spoons");
        item2.setDescription("Silver");
        item2.setOwner(user1);
        item2.setAvailable(true);
        itemRepository.save(item2);

        final Item item3 = new Item();
        item3.setName("Vase Gold");
        item3.setDescription("3 litres");
        item3.setOwner(user1);
        item3.setAvailable(true);
        itemRepository.save(item3);

        List<Item> items = itemRepository.findAllByRequest(itemRequest);

        assertEquals(1, items.size());
        assertEquals("Vase", items.getFirst().getName());
    }
}
