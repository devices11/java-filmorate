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
            DELETE FROM filmorate.reviews_dislikes WHERE reviews_dislike_id = ?
            """;

    private static final String DELETE_ALL_BY_FILM_QUERY = """
            DELETE FROM filmorate.reviews_dislikes
            WHERE review_id IN (SELECT review_id FROM filmorate.reviews WHERE film_id = ?);
            """;

    private static final String DELETE_ALL_BY_REVIEW_QUERY = """
            DELETE FROM filmorate.reviews_dislikes WHERE review_id = ?
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
    public ReviewDislike create(ReviewDislike reviewDislike) {
        long id = insert(INSERT_QUERY, reviewDislike.getUserId(), reviewDislike.getReviewId());
        reviewDislike.setId(id);
        return reviewDislike;
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
}
