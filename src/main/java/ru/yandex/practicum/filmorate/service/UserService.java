package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
        public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUser(Integer id) {
        return userStorage.getUser(id);
    }

    public List<User> getFriends(Integer userId) {
        log.debug("Searching for friends (userId={})", userId);
        return userStorage.getFriendsByUserId(userId);
    }
    public void addFriend(Integer userId, Integer friendId) {
        log.debug("Add friend (id={}) to user (id={})", friendId, userId);
        userStorage.addFriend(userId, friendId);
        log.debug("Friends ({}/{}) updated.", friendId, userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        log.debug("Delete friend (id={}) from user (id={})", friendId, userId);
        userStorage.deleteFriend(userId, friendId);
        log.debug("Users(id:{}/{}) are no longer friends.", friendId, userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
                log.debug("Searching for common users friends (ids:{}/{})", userId, otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }
}
