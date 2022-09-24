package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsDao {

    List<User> getFriendsByUserId(Integer userId);
    void addFriend(Integer userId, Integer friendId);
    void deleteFriend(Integer userId, Integer friendId);
    List<User> getCommonFriends(Integer userId, Integer otherId);
}
