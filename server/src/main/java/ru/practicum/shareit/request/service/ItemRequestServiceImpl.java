package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponceForIR;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponceDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository itemRequestRepository;

    ItemRequestMapper itemRequestMapper;

    UserRepository userRepository;

    UserMapper userMapper;

    ItemRepository itemRepository;

    ItemMapper itemMapper;

    final Sort sort = Sort.by(Sort.Direction.DESC, "created");

    @Override
    @Transactional
    public ItemRequestResponceDto saveItemRequest(Integer userId, ItemRequestDto itemRequestDto) {
        log.info("Создание нового запроса");
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(itemRequestDto,
                user));
        log.info("Успешно создан запрос на вещь");
        final ItemRequestResponceDto itemRequestResponceDto =
                itemRequestMapper.toItemRequestResponceDto(itemRequest);
        itemRequestResponceDto.setRequestor(userMapper.toUserDto(user));
        return itemRequestResponceDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponceDto> getAllByUser(Integer userId) {
        log.info("Запрос на получение всех запросов пользователя с id = {} и все ответы на них", userId);
        final List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId, sort);
        List<ItemRequestResponceDto> list = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            final ItemRequestResponceDto itemRequestResponceDto =
                    itemRequestMapper.toItemRequestResponceDto(itemRequest);
            final List<ItemDtoResponceForIR> items = itemRepository.findAllByRequest(itemRequest)
                    .stream().map(itemMapper::toItemDtoResponceForIR).toList();
            itemRequestResponceDto.setItems(items);
            itemRequestResponceDto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
            list.add(itemRequestResponceDto);
        }
        log.info("Все запросы пользователя с id = {} с предложенными вещами", userId);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponceDto> getAll(Integer userId) {
        log.info("Запрос на получение всех запросов, кроме тех что сделал пользователь с id = {}",
                userId);
        final List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, sort);
        log.info("Получены все запросы кроме запросов пользователя с id = {}", userId);
        final List<ItemRequestResponceDto> list = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
           final ItemRequestResponceDto itemRequestResponceDto =
                   itemRequestMapper.toItemRequestResponceDto(itemRequest);
           itemRequestResponceDto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
           list.add(itemRequestResponceDto);
       }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponceDto getById(Integer requestId) {
        log.info("Запрос на получение запроса с id = {} и все ответы на него", requestId);
        final ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id = {} нет." + requestId));
        final ItemRequestResponceDto itemRequestResponceDto =
                itemRequestMapper.toItemRequestResponceDto(itemRequest);
        final List<ItemDtoResponceForIR> items = itemRepository.findAllByRequest(itemRequest)
                .stream().map(itemMapper::toItemDtoResponceForIR).toList();
        itemRequestResponceDto.setItems(items);
        itemRequestResponceDto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
        log.info("Получен запрос с id = {} и все ответы на него", requestId);
        return itemRequestResponceDto;
    }
}
