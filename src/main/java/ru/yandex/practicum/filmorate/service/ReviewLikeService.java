package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikeDbStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeDbStorage reviewLikeDbStorage;

    public Optional<ReviewLike> find(Long userId, Long reviewId) {
        return reviewLikeDbStorage.find(userId, reviewId);
    }

    public ReviewLike create(ReviewLike reviewLike) {
        return reviewLikeDbStorage.create(reviewLike);
    }

    public void delete(Long id) {
        reviewLikeDbStorage.delete(id);
    }
}
