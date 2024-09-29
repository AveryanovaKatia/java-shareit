package ru.practicum.shareitgateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;
import ru.practicum.shareitgateway.item.dto.ItemDto;
import ru.practicum.shareitgateway.item.dto.CommentDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> save(final Integer ownerId,
                                       final ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(final Integer ownerId,
                                         final Integer itemId,
                                         final ItemDto itemDto) {
        return patch("/" + ownerId, itemId, itemDto);
    }

    public ResponseEntity<Object> findById(final Integer ownerId,
                                           final Integer itemId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> delete(final Integer itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> getItemsByOwnerId(final Integer ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> search(final Integer userId,
                                         final String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", Long.valueOf(userId), parameters);
    }

    public ResponseEntity<Object> saveComment(final Integer userId,
                                               final Integer itemId,
                                               final CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}