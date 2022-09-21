package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenresController {

    private final GenresService genresService;

    @Autowired
    public GenresController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    public List<Genre> findAll() {
        log.debug("Arrived GET-request for /genres");
        return genresService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getFilm(@PathVariable("id") Integer id) {
        log.debug("Arrived GET-request for /genres/{}", id);
        return genresService.getGenreById(id);
    }
}
