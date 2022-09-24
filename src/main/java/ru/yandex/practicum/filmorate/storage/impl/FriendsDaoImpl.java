package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendsDaoImpl implements FriendsDao {

    private final JdbcTemplate jdbc;

    public List<User> getFriendsByUserId(Integer userId) {
        log.info("User successfully found. Searching for friends.");
        String sqlQuery = "select * from USERS, FRIENDS_LIST " +
                "where USERS.ID = FRIENDS_LIST.FRIEND_ID AND FRIENDS_LIST.USER_ID = ?";
        return jdbc.query(sqlQuery, (rs, rowNum) -> createUser(rs), userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("User and Friend successfully found. Adding friend.");
        String sqlQuery = "INSERT INTO FRIENDS_LIST (user_id, friend_id) " +
                "values (?, ?)";
        jdbc.update(sqlQuery, userId, friendId);
        log.info("Add friend (id={}) to user (id={}) successfully.", friendId, userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        log.info("User successfully found. Deleting friend.");
        String sqlQuery = "delete from friends_list where USER_ID = ? and friend_id = ?";
        boolean result = jdbc.update(sqlQuery, userId, friendId) > 0;
        if (result) {
            log.info("Delete successfully.");
        } else {
            log.info("Friendship does not exist.");
        }
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        log.info("Users successfully found. Searching for common friends.");
        String sqlQuery = "select * from USERS " +
                "join FRIENDS_LIST FL on USERS.ID = FL.FRIEND_ID " +
                "where USER_ID = ? AND FRIEND_ID In " +
                "(select FRIEND_ID from FRIENDS_LIST " +
                "where user_id=?)";
        return jdbc.query(sqlQuery, (rs, rowNum) -> createUser(rs), userId, otherId);
    }


    private User createUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
