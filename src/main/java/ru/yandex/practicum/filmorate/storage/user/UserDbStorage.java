package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        if (checkUser(userId)) {
            User user = jdbc.queryForObject("select * from users where id = ?", (rs, rowNum) -> createUser(rs), userId);
            log.info("User found.");
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
        log.info("User (id={} | login={}) created successfully.", newUserId, user.getLogin());
        user.setId(newUserId);
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() != null && checkUser(user.getId())) {
            log.info("User successfully found. Trying to update.");
            String sql = "update users set " +
                    "email = ?, login = ?, name = ?, birthday = ? " +
                    "where id = ?";

            jdbc.update(sql,
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
