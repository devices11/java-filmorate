package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewDislike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewDislikeRowMapper implements RowMapper<ReviewDislike> {
    @Override
    public ReviewDislike mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReviewDislike.builder()
                .id(rs.getLong("REVIEWS_DISLIKE_ID"))
                .userId(rs.getLong("USER_ID"))
                .reviewId(rs.getLong("REVIEW_ID"))
                .build();
    }
}
