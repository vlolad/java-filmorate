package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id; //Генератор id для пользователей
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> findAll() {
        log.info("Get request for /users.");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        user.setId(insertId());
        log.info("Create new user: {}", user);
        users.put(user.getId(), user);
        log.info("Create user successfully with id: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Update user: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    private int insertId(){
        id++;
        return id;
    }
}
