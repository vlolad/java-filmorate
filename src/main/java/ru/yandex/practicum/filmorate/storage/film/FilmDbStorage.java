package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmMapper filmMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmMapper filmMapper) {
        this.jdbc = jdbc;
        this.filmMapper = filmMapper;
    }

    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM FILMS, RATINGS WHERE RATINGS.ID = RATING_ID";
        log.info("Searching for all films.");
        List<Film> films = jdbc.query(sqlQuery, filmMapper);
        for (Film film : films) {
            film.setGenres(getFilmGenres(film.getId()));
            film.setRate(getRating(film.getId()));
        }
        return films;
    }

    public Film getFilm(Integer filmId) {
        if (checkFilm(filmId)) {
            log.info("Film found.");
            String sqlQuery = "SELECT * FROM FILMS, RATINGS WHERE FILMS.ID = ? AND RATINGS.ID = RATING_ID";
            Film film = jdbc.queryForObject(sqlQuery, filmMapper, filmId);
            if (film != null) {
                film.setGenres(getFilmGenres(filmId));
                film.setRate(getRating(film.getId()));
            }
            return film;
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
            updateFilmGenres(film.getGenres(), newFilmId);
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
            updateFilmGenres(film.getGenres(), film.getId());
            log.info("Film (id={} | name={}) updated successfully.", film.getId(), film.getName());
            film.setGenres(getFilmGenres(film.getId()));
            film.setRate(getRating(film.getId()));
            return film;
        } else {
            log.warn("Film (id={}) not found.", film.getId());
            throw new NotFoundException("Film with ID + " + film.getId() + " not found.");
        }
    }

    //Кажется, этот метод не очень уместно перемещать в DAO лайков,
    //поскольку он в основном создает фильмы, хоть и на основе сортировки их по лайкам
    public List<Film> getPopular(Integer limit) {
        String sqlQuery = "SELECT FILMS.*, COUNT(FL.LIKE_USER_ID) AS SES, RATINGS.* " +
                "FROM FILMS LEFT JOIN FILMS_LIKES FL on ID = FL.FILM_ID, RATINGS " +
                "WHERE RATING_ID = RATINGS.ID GROUP BY FILMS.ID " +
                "ORDER BY SES DESC " +
                "LIMIT ?";
        log.info("Searching for {} most popular films.", limit);
        return jdbc.query(sqlQuery, filmMapper, limit);
    }

    private boolean checkFilm(Integer id) {
        return jdbc.queryForRowSet("select * from films where id = ?", id).next();
    }

    private Integer getRating(Integer id) {
        String sql = "SELECT COUNT(LIKE_USER_ID) FROM FILMS_LIKES WHERE FILM_ID = ?";
        return jdbc.queryForObject(sql, Integer.class, id);
    }
    private void updateFilmGenres(List<Genre> list, Integer filmId) {
        jdbc.update("DELETE FROM FILMS_GENRES WHERE FILM_ID = ?", filmId);
        if (list != null && !list.isEmpty()) {
            String sql = "INSERT INTO FILMS_GENRES (GENRE_ID, FILM_ID) VALUES (?, ?)";
            List <Genre> finalList = list.stream()
                    .distinct()
                    .collect(Collectors.toList()); // Удаление повторяющихся жанров
            log.info("Insert genres ({}) for film (id={}).", list, filmId);
            for (Genre genre : finalList) {
                jdbc.update(sql, genre.getId(), filmId);
            }
        }
    }

    private List<Genre> getFilmGenres(Integer filmId) {
        String sqlQuery = "SELECT G.ID, G.NAME FROM GENRES AS G " +
                "JOIN FILMS_GENRES FG on G.ID = FG.GENRE_ID " +
                "WHERE FILM_ID = ?";
        log.info("Searching for film (id={}) genres.", filmId);
        return jdbc.query(sqlQuery, (rs, rowNum) -> createGenre(rs), filmId);
    }

    private Genre createGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
