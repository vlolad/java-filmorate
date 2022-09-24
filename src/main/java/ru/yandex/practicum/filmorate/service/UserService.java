package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendsDao friendsDao;
    private final JdbcTemplate jdbc;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, FriendsDao friendsDao,
                       JdbcTemplate jdbc) {
        this.userStorage = userStorage;
        this.friendsDao = friendsDao;
        this.jdbc = jdbc;
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
        if (checkUser(userId)) {
            log.debug("Searching for friends (userId={})", userId);
            return friendsDao.getFriendsByUserId(userId);
        } else {
            log.warn("User with such id (id={}) not found.", userId);
            throw new NotFoundException("User not found.");
        }
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (checkUser(userId) && checkUser(friendId)) {
            log.debug("Add friend (id={}) to user (id={})", friendId, userId);
            friendsDao.addFriend(userId, friendId);
            log.debug("Friends ({}/{}) updated.", friendId, userId);
        } else {
            log.warn("User or Friend with such id not found.");
            throw new NotFoundException("User or Friend not found.");
        }

    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (checkUser(userId)) {
            log.debug("Delete friend (id={}) from user (id={})", friendId, userId);
            friendsDao.deleteFriend(userId, friendId);
            log.debug("Users(id:{}/{}) are no longer friends.", friendId, userId);
        } else {
            log.warn("User with such id (id={}) not found.", userId);
            throw new NotFoundException("User not found.");
        }

    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        if (checkUser(userId) && checkUser(otherId)) {
            log.debug("Searching for common users friends (ids:{}/{})", userId, otherId);
            return friendsDao.getCommonFriends(userId, otherId);
        } else {
            log.warn("User or OtherUser with such id not found.");
            throw new NotFoundException("One of users (or both) not found.");
        }
    }

    private boolean checkUser(Integer id) {
        return jdbc.queryForRowSet("select * from users where id = ?", id).next();
    }
}
