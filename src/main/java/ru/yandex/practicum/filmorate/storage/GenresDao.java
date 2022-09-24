package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenresDao {

    Genre getGenreById(Integer genreId);
    List<Genre> getAllGenres();
    List<Genre> getFilmGenres(Integer filmId);
    void updateFilmGenres(List<Genre> list, Integer filmId);
}
