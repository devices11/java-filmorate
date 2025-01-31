package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewDbStorageImpl extends BaseStorage<Review> implements ReviewDbStorage {
    private final ReviewRowMapper reviewRowMapper;

    private static final String FIND_BY_ID_QUERY = """
            SELECT * FROM filmorate.reviews WHERE review_id = ?
            """;

    private static final String FIND_REVIEWS_QUERY = """
            SELECT * FROM filmorate.reviews
            WHERE (? IS NULL OR film_id = ?)
            ORDER BY useful DESC LIMIT ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO filmorate.reviews(content, film_id, user_id, is_positive, useful)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_QUERY = """
            UPDATE filmorate.reviews
            SET content = ?, is_positive = ? WHERE review_id = ?
            """;

    private static final String DELETE_QUERY = """
            DELETE FROM FILMORATE.reviews WHERE review_id = ?
            """;

    public ReviewDbStorageImpl(JdbcOperations jdbc, ReviewRowMapper reviewRowMapper) {
        super(jdbc);
        this.reviewRowMapper = reviewRowMapper;
    }

    @Override
    public Optional<Review> findById(Long id) {
        return findOne(reviewRowMapper, FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<Review> findReviews(Long filmId, int count) {
        return findMany(reviewRowMapper, FIND_REVIEWS_QUERY, filmId, filmId, count);
    }

    @Override
    public Review create(Review review) {
        long id = insert(INSERT_QUERY,
                review.getContent(),
                review.getFilmId(),
                review.getUserId(),
                review.getIsPositive(),
                review.getUseful()
        );
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        return review;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }
}
