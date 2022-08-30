package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
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
        if (films.containsKey(id)) {
            log.debug("Success!");
            return films.get(id);
        } else {
            log.warn("Film not found.");
            throw new NotFoundException("Film with id " + id + " not found.");
        }
    }

    public Film createFilm(Film film) {
        checkInput(film);
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
        checkInput(film);
        log.debug("Put film: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    private Integer insertId() {
        return id++;
    }

    private void checkInput (Film film) {
        if (film.getReleaseDate().isBefore(film.getMinReleaseDate())) {
            log.error("Film release date is before minimal release date.");
            throw new ValidationException("Film is unusually old.");
        }
    }
}
