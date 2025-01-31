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
            DELETE FROM filmorate.reviews_likes WHERE user_id = ? AND review_id = ?
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
    public void create(Long userId, Long reviewId) {
        insert(INSERT_QUERY, userId, reviewId);
    }

    @Override
    public void delete(Long userId, Long reviewId) {
        delete(DELETE_QUERY, userId, reviewId);
    }
}
