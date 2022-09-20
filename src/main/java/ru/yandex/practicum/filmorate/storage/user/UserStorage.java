package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();
    User createUser(User user);
    User updateUser(User user);
    User getUser(Integer userId);
    List<User> getFriendsByUserId (Integer userId);
    void addFriend(Integer userId, Integer friendId);
    void deleteFriend(Integer userId, Integer friendId);
    List<User> getCommonFriends(Integer userId, Integer otherId);
}
