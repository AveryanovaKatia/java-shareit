package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {

        final ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        return itemRequest;
    }

    public ItemRequestResponceDto toItemRequestResponceDto(ItemRequest itemRequest) {

        final ItemRequestResponceDto itemRequestResponceDto = new ItemRequestResponceDto();

        itemRequestResponceDto.setId(itemRequest.getId());
        itemRequestResponceDto.setCreated(itemRequest.getCreated());
        itemRequestResponceDto.setDescription(itemRequest.getDescription());

        return itemRequestResponceDto;
    }
}
