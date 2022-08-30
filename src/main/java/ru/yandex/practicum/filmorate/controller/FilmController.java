package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Arrived GET-request for /films.");
        return filmStorage.getFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Arrived POST-request for /films.");
        log.info(film.toString());
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Arrived PUT-request for /films.");
        log.info(film.toString());
        return filmStorage.updateFilm(film);
    }
}
