package ru.practicum.shareit.item.dto;

import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Setter
@Component
public class ItemMapper {

    public Item toItem(final User owner, final ItemDto itemDto) {

        final Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(owner);
        item.setAvailable(itemDto.getAvailable());

        return item;
    }

    public ItemDto toItemDto(final Item item) {

        final ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }

    public ItemResponce toItemResponce(final Item item, List<CommentDto> comments) {

        final ItemResponce itemResponce = new ItemResponce();

        itemResponce.setId(item.getId());
        itemResponce.setName(item.getName());
        itemResponce.setDescription(item.getDescription());
        itemResponce.setAvailable(item.getAvailable());
        itemResponce.setComments(comments);

        return itemResponce;
    }

    public ItemDtoResponceForIR toItemDtoResponceForIR(Item item) {

        final ItemDtoResponceForIR itemDtoResponceForIR = new ItemDtoResponceForIR();

        itemDtoResponceForIR.setItemId(item.getId());
        itemDtoResponceForIR.setOwnerId(item.getOwner().getId());
        itemDtoResponceForIR.setName(item.getName());

        return itemDtoResponceForIR;
    }
}
