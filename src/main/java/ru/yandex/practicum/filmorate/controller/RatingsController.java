package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.RatingsService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingsController {

    private final RatingsService ratingsService;

    @GetMapping
    public List<MPA> findAll() {
        log.debug("Arrived GET-request for /mpa");
        return ratingsService.getAll();
    }

    @GetMapping("/{id}")
    public MPA getRating(@PathVariable("id") Integer id) {
        log.debug("Arrived GET-request for /mpa/{}", id);
        return ratingsService.getMpaById(id);
    }
}
