package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    public UserDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> getUsers() {
        String sqlQuery = "select * from users";
        log.info("Searching for users in database...");
        return jdbc.query(sqlQuery, (rs, rowNum) -> createUser(rs));
    }

    public User getUser(Integer userId) {
        SqlRowSet userRs = jdbc.queryForRowSet("select * from users where id = ?", userId);
        if (userRs.next()) {
            User user = new User(
                    userRs.getInt("id"),
                    userRs.getString("email"),
                    userRs.getString("login"),
                    userRs.getString("name"),
                    Objects.requireNonNull(userRs.getDate("birthday")).toLocalDate()
            );
            log.info("Found user: " + user);
            return user;
        } else {
            log.warn("User id={} not found.", userId);
            throw new NotFoundException("User with id " + userId + " not found.");
        }
    }

    public User createUser(User user) {
        // Чтобы узнать, какой id был присвоен юзеру в БД, используется SimpleJdbcInsert
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Integer newUserId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        log.info("User (id={} | login={}) create successfully.", newUserId, user.getLogin());
        user.setId(newUserId);
        return user;
    }

    public User updateUser(User user) {
        SqlRowSet checkUser = jdbc.queryForRowSet("select * from users where id = ?", user.getId());
        if (user.getId() != null && checkUser.next()) {
            log.info("User successfully found. Trying to update.");
            String sqlQuery = "update users set " +
                    "email = ?, login = ?, name = ?, birthday = ? " +
                    "where id = ?";

            jdbc.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("User (id={} | login={}) updated successfully.", user.getId(), user.getLogin());
            return user;
        } else {
            log.warn("User with such id (id={}) not found.", user.getId());
            throw new NotFoundException("User not found.");
        }
    }

    public List<User> getFriendsByUserId(Integer userId) {
        if (checkUser(userId)) {
            log.info("User successfully found. Searching for friends.");
            String sqlQuery = "Select * from USERS " +
                    "join FRIENDS_LIST as fl on USERS.ID = fl.FRIEND_ID " +
                    "where fl.USER_ID = ? " +
                    "order by id";
            return jdbc.query(sqlQuery, (rs, rowNum) -> createUser(rs), userId);
        } else {
            log.warn("User with such id (id={}) not found.", userId);
            throw new NotFoundException("User not found.");
        }
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (checkUser(userId) && checkUser(friendId)) {
            log.info("User and Friend successfully found. Adding friend.");
            String sqlQuery = "INSERT INTO FRIENDS_LIST (user_id, friend_id) " +
                    "values (?, ?)";
            jdbc.update(sqlQuery, userId, friendId);
            log.info("Add friend (id={}) to user (id={}) successfully.", friendId, userId);
        } else {
            log.warn("User or Friend with such id not found.");
            throw new NotFoundException("User or Friend not found.");
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (checkUser(userId)) {
            log.info("User successfully found. Deleting friend.");
            String sqlQuery = "delete from friends_list where USER_ID = ? and friend_id = ?";
            boolean result = jdbc.update(sqlQuery, userId, friendId) > 0;
            if (result) {
                log.info("Delete successfully.");
            } else {
                log.info("Friendship does not exist.");
            }
        } else {
            log.warn("User with such id (id={}) not found.", userId);
            throw new NotFoundException("User not found.");
        }
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        if (checkUser(userId) && checkUser(otherId)) {
            log.info("Users successfully found. Searching for common friends.");
            String sqlQuery = "select * from USERS " +
                    "join FRIENDS_LIST FL on USERS.ID = FL.FRIEND_ID " +
                    "where USER_ID = ? AND FRIEND_ID In " +
                    "(select FRIEND_ID from FRIENDS_LIST " +
                    "where user_id=?)";
            return jdbc.query(sqlQuery, (rs, rowNum) -> createUser(rs), userId, otherId);
        } else {
            log.warn("User or OtherUser with such id not found.");
            throw new NotFoundException("One of users (or both) not found.");
        }
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

    private boolean checkUser(Integer id) {
        return jdbc.queryForRowSet("select * from users where id = ?", id).next();
    }
}
