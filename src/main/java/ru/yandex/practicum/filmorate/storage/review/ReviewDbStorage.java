package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

public interface ReviewDbStorage {
    Optional<Review> findById(Long id);

    Review create(Review review);

    Review update(Review review);

    void delete(Long id);
}
