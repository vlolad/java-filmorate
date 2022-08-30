package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        checkName(user);
        user.setId(insertId());
        log.debug("Create new user: {}", user);
        users.put(user.getId(), user);
        log.info("Create user successfully with id: {}", user.getId());
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("User with such id not found.");
            throw new NotFoundException("User not found.");
        }
        checkName(user);
        log.info("Update user: {}", user);
        users.put(user.getId(), user);
        log.debug("User with id={} updated successfully", user.getId());
        return user;
    }

    private int insertId(){
        return id++;
    }

    private void checkName(User user) {
        user.setLogin(user.getLogin().trim()); // Убирает лишние пробелы перед и после логина, на валидацию не влияет
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
