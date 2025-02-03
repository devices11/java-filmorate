package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewDbStorage {
    Optional<Review> findById(Long id);

    Review create(Review review);

    Review update(Review review);

    void delete(Long id);

    Collection<Review> findReviews(Long filmId, int count);

    Integer updateUsefulByLikesByUserIdForDelete(Long id);

    Integer updateUsefulByDislikesByUserIdForDelete(Long id);
}
