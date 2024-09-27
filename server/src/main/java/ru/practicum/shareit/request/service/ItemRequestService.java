package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponceDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponceDto saveItemRequest(final Integer userId, final ItemRequestDto itemRequestDto);

    List<ItemRequestResponceDto> getAllByUser(final Integer userId);

    List<ItemRequestResponceDto> getAll(final Integer userId);

    ItemRequestResponceDto getById(final Integer requestId);

}
