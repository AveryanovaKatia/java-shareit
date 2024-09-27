package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.item.dto.ItemResponce;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {

    UserRepository userRepository;

    ItemRepository itemRepository;

    ItemMapper itemMapper;

    CommentRepository commentRepository;

    CommentMapper commentMapper;

    BookingRepository bookingRepository;

    ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto save(final Integer ownerId, final ItemDto itemDto) {
        log.info("Запрос на добавление новой вещи");
        final User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + ownerId));
        final Item item = itemRepository.save(itemMapper.toItem(owner, itemDto));
        if (Objects.nonNull(itemDto.getRequestId())) {
            final ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запроса с id = {} нет." + itemDto.getRequestId()));
        item.setRequest(itemRequest);
        }
        log.info("Вещь успешно добавлена под id {}", item.getId());
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(final Integer ownerId, final Integer itemId, final ItemDto itemDto) {
        log.info("Запрос на обновление вещи");
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id = {} нет." + itemId));
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            log.warn("Только владелец может редактировать данные о вещи." +
                    "Пользователь с id = {} не владелец вещи с id = {}", ownerId, itemId);
            throw new ForbiddenException("Только владелец может редактировать данные о вещи." +
                    "Пользователь с id = " + ownerId + " не владелец вещи с id = " + itemId);
        }
        if (Objects.nonNull(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if ((Objects.nonNull(itemDto.getDescription()))) {
            item.setDescription(itemDto.getDescription());
        }
        if ((Objects.nonNull(itemDto.getAvailable()))) {
            item.setAvailable(itemDto.getAvailable());
        }
        final Item updateItem = itemRepository.save(item);
        log.info("Вещь с id {} успешно обновлена", updateItem.getId());
        return itemMapper.toItemDto(updateItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponce findById(final Integer ownerId, final Integer itemId) {
        log.info("Запрос на получение вещи с id {}", itemId);
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id = {} нет." + itemId));
        List<CommentDto> commentsDto = commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto).collect(Collectors.toList());
        final ItemResponce itemResponce = itemMapper.toItemResponce(item, commentsDto);
        if (item.getOwner().getId().equals(ownerId)) {
            Optional<Booking> last = bookingRepository.findTopByItemIdAndEndBeforeAndStatusInOrderByEndDesc(itemId,
                    LocalDateTime.now(), List.of(Status.APPROVED));
            itemResponce.setLastBooking(last.map(Booking::getEnd).orElse(null));

            Optional<Booking> future = bookingRepository.findTopByItemIdAndStartAfterAndStatusInOrderByStartAsc(itemId,
                    LocalDateTime.now(), List.of(Status.APPROVED));
            itemResponce.setNextBooking(future.map(Booking::getStart).orElse(null));
        }
        log.info("Вещь с id {} успешно получена", itemId);
        return itemResponce;
    }

    @Override
    @Transactional
    public void delete(Integer itemId) {
        log.info("Запрос на удаление вещи с id {}", itemId);
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id = {} нет." + itemId));
        itemRepository.delete(item);
        log.info("Вещь с id {} успешно удалена ", itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwnerId(final Integer ownerId) {
        log.info("Запрос на получение всех вещей пользователя с id {}", ownerId);
        final List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        log.info("Вещи пользователя с id {} успешно получены", ownerId);
        return items.stream().map(itemMapper::toItemDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(final String text) {
        log.info("Поиск вещей по имени или описанию");
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text.trim().toLowerCase())
                .stream().map(itemMapper::toItemDto).toList();
    }

    @Override
    @Transactional
    public CommentDto saveComment(Integer userId, Integer itemId, CommentDto commentDto) {
        log.info("Пользователь с id {} желает оставить комментарий к вещи с id {} ", userId, itemId);
        final User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id = {} нет." + itemId));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId,
                        Status.APPROVED, LocalDateTime.now())
                .isEmpty()) {
            throw new ValidationException("Нельзя написать отзыв если пользователь не брал в аренду вещь");
        }
        final Comment comment = commentMapper.toComment(commentDto, owner, item);
        commentRepository.save(comment);
        log.info("Пользователь с id {} оставил комментарий к вещи с id {} ", userId, itemId);
        return commentMapper.toCommentDto(comment);
    }
}
