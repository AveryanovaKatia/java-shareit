package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRepositoryImpl implements UserRepository {

    Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(final User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(final Integer userId, final UserDto userDto) {
       final User user = users.get(userId);
       if (Objects.nonNull(userDto.getName())) {
           user.setName(userDto.getName());
        }
        if (Objects.nonNull(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }
        users.put(userId, user);
        return user;
    }

    @Override
    public Optional<User> getById(final Integer userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public void delete(final Integer userId) {
        users.remove(userId);
    }

    private Integer getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
