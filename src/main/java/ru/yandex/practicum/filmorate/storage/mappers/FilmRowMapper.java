package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film result = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(
                        resultSet.getDate("release_date") != null
                                ? resultSet.getDate("release_date").toLocalDate()
                                : null
                )
                .duration(resultSet.getInt("duration"))
                .build();

        Integer mpaFromDB = resultSet.getInt("mpa_id");
        if (mpaFromDB != null) {
            result.setMpa(Mpa.builder()
                    .id(mpaFromDB)
                    .name(resultSet.getString("mpa_name"))
                    .build());
        }

        return result;
    }

}
