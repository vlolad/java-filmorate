package ru.yandex.practicum.filmorate.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;


import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmMapper implements RowMapper<Film> {

    private final JdbcTemplate jdbc;

    @Autowired
    public FilmMapper(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration")
        );
        if (rs.getInt("rating_id") > 0) {
            film.setMpa(new MPA(rs.getInt("ratings.id"), rs.getString("ratings.name")));
        }
        film.setRate(getRating(film.getId()));
        return film;
    }

    private Integer getRating(Integer id) {
        String sql = "SELECT COUNT(LIKE_USER_ID) FROM FILMS_LIKES WHERE FILM_ID = ?";
        return jdbc.queryForObject(sql, Integer.class, id);
    }
}
