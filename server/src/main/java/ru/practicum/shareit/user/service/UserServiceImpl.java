package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    UserMapper userMapper;

    @Override
    @Transactional
    public UserDto save(final UserDto userDto) {
        log.info("Запрос на добавление пользователя ");
        final User user = userRepository.save(userMapper.toUser(userDto));
        log.info("Пользователь успешно добавлен под id {}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(final Integer userId, final UserDto userDto) {
        log.info("Запрос на обновление пользователя ");
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        if (Objects.nonNull(userDto.getName())) {
            user.setName(userDto.getName());
        }
        if (Objects.nonNull(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        final User updateUser = userRepository.save(user);
        log.info("Пользователь с id {} успешно обновлен", updateUser.getId());
        return userMapper.toUserDto(updateUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(final Integer userId) {
        log.info("Запрос на получение пользователя под id {}", userId);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        log.info("Запрос на получение списка пользователей");
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Override
    @Transactional
    public void delete(final Integer userId) {
        log.info("Запрос на удаление пользователей с id {}", userId);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        userRepository.delete(user);
        log.info("Пользователь с id {} успешно удален ", userId);
    }
}
