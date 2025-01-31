package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewDislike;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewDislikeRowMapper;

import java.util.Optional;

@Repository
public class ReviewDislikeDbStorageImpl extends BaseStorage<ReviewDislike> implements ReviewDislikeDbStorage {
    private final ReviewDislikeRowMapper reviewDislikeRowMapper;

    private static final String FIND_QUERY = """
            SELECT * FROM filmorate.reviews_dislikes WHERE user_id = ? AND review_id = ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO filmorate.reviews_dislikes (user_id, review_id) VALUES (?, ?)
            """;

    private static final String DELETE_QUERY = """
            DELETE FROM filmorate.reviews_dislikes WHERE user_id = ? AND review_id = ?
            """;

    public ReviewDislikeDbStorageImpl(JdbcOperations jdbc, ReviewDislikeRowMapper reviewDislikeRowMapper) {
        super(jdbc);
        this.reviewDislikeRowMapper = reviewDislikeRowMapper;
    }

    @Override
    public Optional<ReviewDislike> find(Long userId, Long reviewId) {
        return findOne(reviewDislikeRowMapper, FIND_QUERY, userId, reviewId);
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
