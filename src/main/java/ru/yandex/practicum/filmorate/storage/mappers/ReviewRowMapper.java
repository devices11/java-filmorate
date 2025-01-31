package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("REVIEW_ID"))
                .content(rs.getString("CONTENT"))
                .filmId(rs.getLong("FILM_ID"))
                .userId(rs.getLong("USER_ID"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .useful(rs.getInt("USEFUL"))
                .build();
    }
}
