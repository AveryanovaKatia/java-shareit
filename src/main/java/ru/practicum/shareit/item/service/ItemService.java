package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(final Integer ownerId, final ItemDto itemDto);

    ItemDto update(final Integer ownerId, final Integer itemId, final ItemDto itemDto);

    ItemDto getById(final Integer itemId);

    void delete(final Integer itemId);

    List<ItemDto> getOwnerItems(final Integer ownerId);

    List<ItemDto> search(final String text);

    void deleteAll();
}
