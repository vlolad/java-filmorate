package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
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


    public void addFriend(Integer userId, Integer friendId) {
        log.debug("Add friend (id={}) to user (id={})", friendId, userId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
        log.debug("Friends ({}/{}) updated.", friendId, userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        log.debug("Delete friend (id={}) from user (id={})", friendId, userId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
        log.debug("Users(id:{}/{}) are no longer friends.", friendId, userId);
    }

    public List<User> getFriends(Integer userId) {
        User user = userStorage.getUser(userId);
        log.debug("Searching for friends (userId={})", userId);
        return userStorage.getUsers().stream()
                .filter(p -> user.getFriendList().contains(p.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = userStorage.getUser(userId);
        User otherUser = userStorage.getUser(otherId);
        log.debug("Searching for common users friends (ids:{}/{})", userId, otherId);
        return userStorage.getUsers().stream()
                .filter(p -> user.getFriendList().contains(p.getId())&&
                        otherUser.getFriendList().contains(p.getId()))
                .collect(Collectors.toList());
    }
}
