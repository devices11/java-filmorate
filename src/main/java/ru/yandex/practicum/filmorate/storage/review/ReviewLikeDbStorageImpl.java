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

    private static final String DELETE_ALL_BY_FILM_QUERY = """
            DELETE FROM filmorate.reviews_likes
            WHERE review_id IN (SELECT review_id FROM filmorate.reviews WHERE film_id = ?)
            """;

    private static final String DELETE_ALL_BY_REVIEW_QUERY = """
            DELETE FROM filmorate.reviews_likes WHERE review_id = ?
            """;

    private static final String DELETE_QUERY = """
            DELETE FROM filmorate.reviews_likes WHERE reviews_like_id = ?
            """;

    private static final String DELETE_ALL_BY_USER_ID = """
            DELETE FROM filmorate.reviews_likes WHERE user_id = ?
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

    @Override
    public void deleteAllByFilmId(Long filmId) {
        delete(DELETE_ALL_BY_FILM_QUERY, filmId);
    }

    @Override
    public void deleteAllByReviewId(Long reviewId) {
        delete(DELETE_ALL_BY_REVIEW_QUERY, reviewId);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        delete(DELETE_ALL_BY_USER_ID, userId);
    }
}
