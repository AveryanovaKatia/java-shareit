package ru.practicum.shareit.item.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRepositoryImpl implements ItemRepository {

    Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item create(final Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(final Integer itemId, final ItemDto itemDto) {
        final Item item = items.get(itemId);
        if (Objects.nonNull(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if ((Objects.nonNull(itemDto.getDescription()))) {
            item.setDescription(itemDto.getDescription());
        }
        if  ((Objects.nonNull(itemDto.getAvailable()))) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(itemId, item);
        return item;
    }

    @Override
    public Optional<Item> getById(final Integer itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public void delete(Integer itemId) {
        items.remove(itemId);
    }

    @Override
    public List<Item> getOwnerItems(final Integer ownerId) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId)).toList();
    }

    @Override
    public List<Item> search(final String text) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                .toList();
    }

    @Override
    public void deleteAll() {
        items.clear();
    }

    private Integer getNextId() {
        int currentMaxId = items.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
