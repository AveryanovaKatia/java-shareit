package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("CommentRepository_empty")
    public void testEmpty() {

        final List<Comment> comments = commentRepository.findAll();

        assertTrue(comments.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("CommentRepository_findAllByItemId_ThereAreComments")
    public void testFindAllByItemId_ThereAreComments() {

        final User user = new User();
        user.setName("Katia");
        user.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user);

        final User user1 = new User();
        user1.setName("Olia");
        user1.setEmail("molnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Tania");
        user2.setEmail("grom@yandex.ru");
        userRepository.save(user2);

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

        final Comment comment = new Comment();
        comment.setText("Bla-bla-bla");
        comment.setItem(item);
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        final Comment comment1 = new Comment();
        comment1.setText("Tra-ta-ta");
        comment1.setItem(item1);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        final Comment comment2 = new Comment();
        comment2.setText("Tra-ta-ta-bla-bla");
        comment2.setItem(item1);
        comment2.setAuthor(user2);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        final List<Comment> comments = commentRepository.findAllByItemId(2);

        assertEquals(2, comments.size());
    }

    @Test
    @DirtiesContext
    @DisplayName("CommentRepository_findAllByItemId_ThereAreNotComments")
    public void testFindAllByItemId_ThereAreNotComments() {

        final User user = new User();
        user.setName("Katia");
        user.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user);

        final User user1 = new User();
        user1.setName("Olia");
        user1.setEmail("molnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Tania");
        user2.setEmail("grom@yandex.ru");
        userRepository.save(user2);

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

        final Comment comment = new Comment();
        comment.setText("Bla-bla-bla");
        comment.setItem(item);
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        final Comment comment1 = new Comment();
        comment1.setText("Tra-ta-ta");
        comment1.setItem(item1);
        comment1.setAuthor(user);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        final Comment comment2 = new Comment();
        comment2.setText("Tra-ta-ta-bla-bla");
        comment2.setItem(item1);
        comment2.setAuthor(user2);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        final List<Comment> comments = commentRepository.findAllByItemId(3);

        assertTrue(comments.isEmpty());
    }
}
