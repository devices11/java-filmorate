package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Optional;

public interface ReviewLikeDbStorage {
    Optional<ReviewLike> find(Long userId, Long reviewId);

    ReviewLike create(ReviewLike reviewLike);

    void delete(Long id);

    void deleteAllByFilmId(Long filmId);

    void deleteAllByReviewId(Long reviewId);
}
