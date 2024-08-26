package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item create(final Item item);

    Item update(final Integer itemId, final ItemDto itemDto);

    Optional<Item> getById(final Integer itemId);

    void delete(final Integer itemId);

    List<Item> getOwnerItems(final Integer ownerId);

    List<Item> search(final String text);

    void deleteAll();
}
