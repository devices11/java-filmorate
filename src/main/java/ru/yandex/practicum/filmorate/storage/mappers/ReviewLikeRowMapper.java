package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewLikeRowMapper implements RowMapper<ReviewLike> {
    @Override
    public ReviewLike mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReviewLike.builder()
                .id(rs.getLong("REVIEWS_LIKE_ID"))
                .userId(rs.getLong("USER_ID"))
                .reviewId(rs.getLong("REVIEW_ID"))
                .build();
    }
}
