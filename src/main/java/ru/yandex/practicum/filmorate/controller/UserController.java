package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id; //Генератор id для пользователей

    @GetMapping
    public List<User> findAll() {
        log.info("Get request for /users.");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        checkName(user);
        user.setId(insertId());
        log.info("Create new user: {}", user);
        users.put(user.getId(), user);
        log.info("Create user successfully with id: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("User with such id not found.");
            throw new NotFoundException("User not found.");
        }
        checkName(user);
        log.info("Update user: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    private int insertId(){
        id++;
        return id;
    }

    private void checkName(User user) {
        user.setLogin(user.getLogin().trim()); // Убирает лишние пробелы перед и после логина, на валидацию не влияет
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
