package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;

    UserRepository userRepository;

    ItemMapper itemMapper;

    @Override
    public ItemDto create(final Integer ownerId, final ItemDto itemDto) {
        log.info("Запрос на добавление новой вещи");
        final User owner = userRepository.getById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + ownerId));
        final Item item = itemRepository.create(itemMapper.toItem(owner, itemDto));
        log.info("Вещь успешно добавлена под id {}", item.getId());
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(final Integer ownerId, final Integer itemId, final ItemDto itemDto) {
        log.info("Запрос на обновление вещи");
        validOwnerId(ownerId);
        final Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id = {} нет." + itemId));
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            log.warn("Только владелец может редактировать данные о вещи." +
                    "Пользователь с id = {} не владелец вещи с id = {}", ownerId, itemId);
            throw new ForbiddenException("Только владелец может редактировать данные о вещи." +
                    "Пользователь с id = " + ownerId + " не владелец вещи с id = " + itemId);
        }
        final Item newItem = itemRepository.update(itemId, itemDto);
        log.info("Вещь с id {} успешно обновлена", newItem.getId());
        return itemMapper.toItemDto(newItem);

    }

    @Override
    public ItemDto getById(final Integer itemId) {
        log.info("Запрос на получение вещи с id {}", itemId);
        final Item item = itemRepository.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id = {} нет." + itemId));
        return itemMapper.toItemDto(item);
    }

    @Override
    public void delete(Integer itemId) {
        log.info("Запрос на удаление вещи с id {}", itemId);
        validItemId(itemId);
        itemRepository.delete(itemId);
        log.info("Вещь с id {} успешно удалена ", itemId);
    }

    @Override
    public List<ItemDto> getOwnerItems(final Integer ownerId) {
        validOwnerId(ownerId);
        return itemRepository.getOwnerItems(ownerId).stream().map(itemMapper::toItemDto).toList();
    }

    @Override
    public List<ItemDto> search(final String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text.trim().toLowerCase()).stream().map(itemMapper::toItemDto).toList();
    }

    @Override
    public void deleteAll() {
        itemRepository.deleteAll();
    }

    private void validOwnerId(final Integer userId) {
        if (userRepository.getById(userId).isEmpty()) {
            log.warn("Пользователя с id = {} нет.", userId);
            throw new NotFoundException("Пользователя с id = {} нет." + userId);
        }
    }

    private void validItemId(final Integer itemId) {
        if (itemRepository.getById(itemId).isEmpty()) {
            log.warn("Вещи с id = {} нет.", itemId);
            throw new NotFoundException("Вещи с id = {} нет." + itemId);
        }
    }
}
