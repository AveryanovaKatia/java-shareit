package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponce;

import java.util.List;

public interface ItemService {

    ItemDto save(final Integer ownerId, final ItemDto itemDto);

    ItemDto update(final Integer ownerId, final Integer itemId, final ItemDto itemDto);

    ItemResponce findById(final Integer ownerId, final Integer itemId);

    void delete(final Integer itemId);

    List<ItemDto>  getItemsByOwnerId(final Integer ownerId);

    List<ItemDto> search(final String text);

    CommentDto saveComment(final Integer userId, final Integer itemId, final CommentDto commentDto);
}
