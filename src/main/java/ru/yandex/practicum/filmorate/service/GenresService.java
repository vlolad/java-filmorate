package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenresDao;

import java.util.List;

@Slf4j
@Service
public class GenresService {

    private final GenresDao genresDao;

    public GenresService(GenresDao genresDao) {
        this.genresDao = genresDao;
    }

    public List<Genre> getAllGenres() {
        return genresDao.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return genresDao.getGenreById(id);
    }
}
