package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.RatingsDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("RatingsDaoDB")
public class RatingsDaoImpl implements RatingsDao {

    private final JdbcTemplate jdbc;

    public RatingsDaoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public MPA getMpaById(Integer mpaId) {
        log.info("Searching for MPA with id={}", mpaId);
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM RATINGS WHERE ID = ?", mpaId);
        if (rs.next()) {
            log.info("Success.");
            return new MPA(
                    rs.getInt("id"),
                    rs.getString("name")
            );
        } else {
            log.warn("MPA with such id not found.");
            throw new NotFoundException("MPA with ID " + mpaId + " not found");
        }
    }

    public List<MPA> getAllMpa() {
        String sqlQuery = "SELECT * FROM RATINGS";
        log.info("Searching for all MPAs.");
        return jdbc.query(sqlQuery, (rs, rowNum) -> createMpa(rs));
    }

    private MPA createMpa(ResultSet rs) throws SQLException {
        return new MPA(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
