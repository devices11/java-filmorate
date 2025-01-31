package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.ReviewDislike;
import ru.yandex.practicum.filmorate.storage.review.ReviewDislikeDbStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewDislikeService {
    private final ReviewDislikeDbStorage reviewDislikeDbStorage;

    public Optional<ReviewDislike> find(Long userId, Long reviewId) {
        return reviewDislikeDbStorage.find(userId, reviewId);
    }

    public ReviewDislike create(ReviewDislike reviewDislike) {
        return reviewDislikeDbStorage.create(reviewDislike);
    }

    void delete(Long id) {
        reviewDislikeDbStorage.delete(id);
    }
}
