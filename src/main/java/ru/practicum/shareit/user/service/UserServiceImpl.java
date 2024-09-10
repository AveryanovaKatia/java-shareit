package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    @Override
    public UserDto create(final UserDto userDto) {
        log.info("Запрос на добавление пользователя ");
        validEmail(userDto.getEmail());
        final User user = userRepository.create(userMapper.toUser(userDto));
        log.info("Пользователь успешно добавлен под id {}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(final Integer userId, final UserDto userDto) {
        log.info("Запрос на обновление пользователя ");
        validId(userId);
        validEmail(userDto.getEmail());
        final User user = userRepository.update(userId, userDto);
        log.info("Пользователь с id {} успешно обновлен", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(final Integer userId) {
        log.info("Запрос на получение пользователя под id {}", userId);
        final User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Запрос на получение списка пользователей");
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public void delete(final Integer userId) {
        log.info("Запрос на удаление пользователей с id {}", userId);
        validId(userId);
        userRepository.delete(userId);
        log.info("Пользователь с id {} успешно удален ", userId);
    }

    private void validId(final Integer userId) {
        if (userRepository.getById(userId).isEmpty()) {
            log.warn("Пользователя с id = {} нет.", userId);
            throw new NotFoundException("Пользователя с id = {} нет." + userId);
        }
    }

    private void validEmail(String email) {
       for (User user : userRepository.findAll()) {
           if (user.getEmail().equals(email)) {
               log.warn("Пользователь с email = {} уже есть.", email);
               throw new ConflictException("Пользователь с email = {} уже есть." + email);
           }
       }
    }
}
