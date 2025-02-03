package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.ReviewDislike;

import java.util.Optional;

public interface ReviewDislikeDbStorage {
    Optional<ReviewDislike> find(Long userId, Long reviewId);

    ReviewDislike create(ReviewDislike reviewDislike);

    void delete(Long id);

    void deleteAllByFilmId(Long filmId);

    void deleteAllByReviewId(Long reviewId);
}
