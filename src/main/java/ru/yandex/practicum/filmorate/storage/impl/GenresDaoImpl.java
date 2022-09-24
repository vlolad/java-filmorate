package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenresDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("GenresDaoDB")
public class GenresDaoImpl implements GenresDao {
    private final JdbcTemplate jdbc;

    public GenresDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Genre getGenreById(Integer genreId) {
        log.info("Searching for Genre with id={}", genreId);
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM GENRES WHERE ID = ?", genreId);
        if (rs.next()) {
            log.info("Success.");
            return new Genre(
                    rs.getInt("id"),
                    rs.getString("name")
            );
        } else {
            log.warn("Genre with such id not found.");
            throw new NotFoundException("Genre with ID " + genreId + " not found");
        }
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRES";
        log.info("Searching for all Genres.");
        return jdbc.query(sqlQuery, (rs, rowNum) -> createGenre(rs));
    }

    public List<Genre> getFilmGenres(Integer filmId) {
        String sqlQuery = "SELECT G.ID, G.NAME FROM GENRES AS G " +
                "JOIN FILMS_GENRES FG on G.ID = FG.GENRE_ID " +
                "WHERE FILM_ID = ?";
        log.info("Searching for film (id={}) genres.", filmId);
        return jdbc.query(sqlQuery, (rs, rowNum) -> createGenre(rs), filmId);
    }

    public void updateFilmGenres(List<Genre> list, Integer filmId) {
        jdbc.update("DELETE FROM FILMS_GENRES WHERE FILM_ID = ?", filmId); //Удаляем существующие жанры
        //И вставляем новые, если они переданы
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

    private Genre createGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
