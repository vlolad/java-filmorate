package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    public void addLike(Integer id, Integer userId) { //TODO добавить список лайкнутых фильмов пользователю
        User user = userStorage.getUser(userId); // Проверка, существует ли вообще такой пользователь
        Film film = filmStorage.getFilm(id);
        log.debug("Add like (id={}) to film id={}", userId, id);
        film.addLike(userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        User user = userStorage.getUser(userId);
        Film film = filmStorage.getFilm(id);
        if (film.getLikes().contains(userId)) {
            log.debug("Remove like (id={}) from film id={}", userId, id);
            film.removeLike(userId);
        } else {
            log.warn("Like (id={}) not found", userId);
            throw new NotFoundException("Like not found");
        }
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(this::compareForLikes)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compareForLikes(Film p1, Film p2) {
        log.debug("p1 size={}, p2 size={}", p1.getLikes().size(), p2.getLikes().size());
        return p2.getLikes().size() - p1.getLikes().size();
    }
}
