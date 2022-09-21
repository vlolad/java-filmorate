package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Integer id);
    List<Film> getPopular(Integer count);
    void addLike(Integer filmId, Integer userId);
    void deleteLike(Integer filmId, Integer userId);
}
