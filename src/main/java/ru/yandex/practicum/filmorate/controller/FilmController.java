package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id; //Генератор id для фильмов
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

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
            throw new NotFoundException("Film not found");
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
        /* if (film.getName().isBlank()) {
            throw new ValidationException("Name cannot be empty.");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Description should be less that 200 symbols.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Film is unusually old.");
        } else if (film.getDuration() > 0) {
            throw new ValidationException("Film duration should be more than 0 minutes.");
        } */
    }
}
