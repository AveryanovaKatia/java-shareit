package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User create(final User user);

    User update(final Integer userId, final UserDto userDto);

    Optional<User> getById(final Integer userId);

    List<User> findAll();

    void delete(final Integer userId);
}
