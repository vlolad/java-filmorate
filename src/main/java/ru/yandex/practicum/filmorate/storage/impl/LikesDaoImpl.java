package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikesDao;

@Component
@Slf4j
@RequiredArgsConstructor
public class LikesDaoImpl implements LikesDao {

    private final JdbcTemplate jdbc;

    public void addLike(Integer filmId, Integer userId) {
        String sql = "MERGE INTO FILMS_LIKES KEY (FILM_ID, LIKE_USER_ID) VALUES (?, ?)";
        log.info("Add like (id={}) to film id={}", userId, filmId);
        jdbc.update(sql, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM FILMS_LIKES WHERE FILM_ID = ? AND LIKE_USER_ID = ?";
        log.info("Remove like (id={}) from film id={}", userId, filmId);
        jdbc.update(sql, filmId, userId);
    }
}
