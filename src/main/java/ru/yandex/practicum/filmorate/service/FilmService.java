package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.LikesDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesDao likesDao;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       LikesDao likesDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesDao = likesDao;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        try {
            User user = userStorage.getUser(userId); // Проверка
            Film film = filmStorage.getFilm(filmId);
        } catch (NotFoundException e) {
            log.warn("Catch NotFoundException.");
            throw e;
        }
        log.debug("User (id={}) and film (id={}) exists.", userId, filmId);
        likesDao.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        try {
            User user = userStorage.getUser(userId); // Проверка
            Film film = filmStorage.getFilm(filmId);
        } catch (NotFoundException e) {
            log.warn("Catch NotFoundException.");
            throw e;
        }
        log.debug("User (id={}) and film (id={}) exists.", userId, filmId);
        likesDao.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getPopular(count);
    }
}
