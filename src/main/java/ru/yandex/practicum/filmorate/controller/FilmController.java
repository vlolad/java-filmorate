package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id; //Генератор id для фильмов

    @GetMapping
    public List<Film> findAll() {
        log.info("Get request for /films.");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        checkInput(film);
        film.setId(insertId());
        log.info("Add new film: {}", film);
        films.put(film.getId(), film);
        log.info("Film added successfully with id: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Film with such id not found.");
            throw new NotFoundException("Film not found.");
        }
        checkInput(film);
        log.info("Put film: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    private int insertId(){
        id++;
        return id;
    }

    private void checkInput (Film film) {
        if (film.getReleaseDate().isBefore(film.getMinReleaseDate())) {
            log.error("Film release date is before minimal release date.");
            throw new ValidationException("Film is unusually old.");
        }
    }
}
