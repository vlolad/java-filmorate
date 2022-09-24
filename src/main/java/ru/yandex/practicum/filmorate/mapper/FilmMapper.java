package ru.yandex.practicum.filmorate.mapper;

import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;


import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@NoArgsConstructor
public class FilmMapper implements RowMapper<Film> {

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
        return film;
    }
}
