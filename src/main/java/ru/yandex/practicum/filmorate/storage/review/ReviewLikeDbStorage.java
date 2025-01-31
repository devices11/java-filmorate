package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Optional;

public interface ReviewLikeDbStorage {
    Optional<ReviewLike> find(Long userId, Long reviewId);

    void create(Long userId, Long reviewId);

    void delete(Long userId, Long reviewId);
}
