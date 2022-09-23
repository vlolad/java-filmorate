package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;


@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    public List<Film> getFilms() {
        log.debug("Films amount: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film getFilm(Integer id) {
        log.debug("Searching for film with id={}", id);
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("Film with id " + id + " not found."));
    }

    public Film createFilm(Film film) {
        film.setId(insertId());
        log.debug("Add new film: {}", film);
        films.put(film.getId(), film);
        log.debug("Film added successfully with id: {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Film with such id not found.");
            throw new NotFoundException("Film not found.");
        }
        log.debug("Put film: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getPopular(Integer count) {
        log.warn("Functionality not functional.");
        return null;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        log.warn("Functionality not functional.");
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        log.warn("Functionality not functional.");
    }

    private Integer insertId() {
        return id++;
    }
}
