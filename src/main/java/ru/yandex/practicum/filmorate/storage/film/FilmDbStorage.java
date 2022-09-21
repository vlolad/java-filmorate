package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.GenresDao;

import java.util.List;

@Slf4j
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmMapper filmMapper;
    private final GenresDao genresDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmMapper filmMapper, GenresDao genresDao) {
        this.jdbc = jdbc;
        this.filmMapper = filmMapper;
        this.genresDao = genresDao;
    }

    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM FILMS";
        log.info("Searching for all films.");
        return jdbc.query(sqlQuery, filmMapper);
    }

    public Film getFilm(Integer filmId) {
        if (checkFilm(filmId)) {
            log.info("Film found.");
            String sqlQuery = "SELECT * FROM FILMS WHERE ID = ?";
            return jdbc.queryForObject(sqlQuery, filmMapper, filmId);
        } else {
            log.warn("Film (id={}) not found.", filmId);
            throw new NotFoundException("Film with ID " + filmId + " not found.");
        }
    }

    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Integer newFilmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genresDao.updateFilmGenres(film.getGenres(), newFilmId);
        }
        log.info("Film (id={} | name={}) created successfully.", newFilmId, film.getName());
        film.setId(newFilmId);
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getId() != null && checkFilm(film.getId())) {
            log.info("Film successfully found. Trying to update.");
            String sql = "UPDATE FILMS SET " +
                    "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? " +
                    "WHERE ID = ?";

            jdbc.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            genresDao.updateFilmGenres(film.getGenres(), film.getId());
            log.info("Film (id={} | name={}) updated successfully.", film.getId(), film.getName());
            return film;
        } else {
            log.warn("Film (id={}) not found.", film.getId());
            throw new NotFoundException("Film with ID + " + film.getId() + " not found.");
        }
    }

    private boolean checkFilm(Integer id) {
        return jdbc.queryForRowSet("select * from films where id = ?", id).next();
    }
}
