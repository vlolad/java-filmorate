package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage,
                          FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Arrived GET-request for /films");
        return filmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Integer id) {
        log.debug("Arrived GET-request for /films/{}", id);
        return filmStorage.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.debug("Arrived GET-request for /films/popular?count={}", count);
        return filmService.getPopular(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Arrived POST-request for /films");
        log.info(film.toString());
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Arrived PUT-request for /films");
        log.info(film.toString());
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable("id") Integer filmId,
                        @PathVariable("userId") Integer userId) {
        log.debug("Arrived PUT-request for /films/{}/like/{}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable("id") Integer filmId,
                           @PathVariable("userId") Integer userId) {
        log.debug("Arrived DELETE-request for /films/{}/like/{}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }
}
