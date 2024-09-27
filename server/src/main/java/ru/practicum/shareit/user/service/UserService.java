package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(final UserDto userDto);

    UserDto update(final Integer userId, final UserDto userDto);

    UserDto findById(final Integer userId);

    List<UserDto> findAll();

    void delete(final Integer userId);
}
