package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewLikeRowMapper;

import java.util.Optional;

@Repository
public class ReviewLikeDbStorageImpl extends BaseStorage<ReviewLike> implements ReviewLikeDbStorage {
    private final ReviewLikeRowMapper reviewLikeRowMapper;

    private static final String FIND_QUERY = """
            SELECT * FROM filmorate.reviews_likes WHERE user_id = ? AND review_id = ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO filmorate.reviews_likes (user_id, review_id) VALUES (?, ?)
            """;

    private static final String DELETE_QUERY = """
            DELETE FROM filmorate.reviews_likes WHERE reviews_like_id = ?
            """;

    public ReviewLikeDbStorageImpl(JdbcOperations jdbc, ReviewLikeRowMapper reviewLikeRowMapper) {
        super(jdbc);
        this.reviewLikeRowMapper = reviewLikeRowMapper;
    }

    @Override
    public Optional<ReviewLike> find(Long userId, Long reviewId) {
        return findOne(reviewLikeRowMapper, FIND_QUERY, userId, reviewId);
    }

    @Override
    public ReviewLike create(ReviewLike reviewLike) {
        long id = insert(INSERT_QUERY, reviewLike.getUserId(), reviewLike.getReviewId());
        reviewLike.setId(id);
        return reviewLike;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }
}
