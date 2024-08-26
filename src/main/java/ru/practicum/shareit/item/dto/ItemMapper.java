package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {

    public ItemDto toItemDto(final Item item) {
        final ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        //itemDto.setRequest(item.getRequest() != null ? item.getRequest().getId() : null);
        //itemDto.setCountShare(item.getCountShare()); появится с бд
        return itemDto;
    }

    public Item toItem(final User owner, final ItemDto itemDto) {
        final Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(owner);
        item.setAvailable(itemDto.getAvailable());
        //item.setRequest(itemDto.getRequest() != null ? item.getRequest().getId() : null);
        return item;
    }
}
