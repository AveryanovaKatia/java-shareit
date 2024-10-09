package ru.practicum.shareitgateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.booking.dto.BookingRequest;
import ru.practicum.shareitgateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> saveRequest(final Integer userId,
                                              final BookingRequest bookingRequest) {
        return post("", userId, bookingRequest);
    }

    public ResponseEntity<Object> approved(final Integer userId,
                                           final Integer bookingId,
                                           final boolean approved) {
        final Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId.longValue(), parameters, null);
    }

    public ResponseEntity<Object> findById(final Integer userId,
                                           final Integer bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> findAllByUserId(final Integer userId,
                                                  final String state) {
        final Map<String, Object> parameters = Map.of("state", state);
        return get("?state={state}", userId.longValue(), parameters);
    }

    public ResponseEntity<Object> findAllByOwnerId(final Integer userId,
                                                       final String state) {
        final Map<String, Object> parameters = Map.of("state", state);
        return get("/owner?state={state}", userId.longValue(), parameters);
    }
}
