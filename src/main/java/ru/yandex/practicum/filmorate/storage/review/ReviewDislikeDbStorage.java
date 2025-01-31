package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.ReviewDislike;

import java.util.Optional;

public interface ReviewDislikeDbStorage {
    Optional<ReviewDislike> find(Long userId, Long reviewId);

    void create(Long userId, Long reviewId);

    void delete(Long userId, Long reviewId);
}
