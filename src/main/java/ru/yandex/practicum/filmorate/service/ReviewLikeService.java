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

    public void create(Long userId, Long reviewId) {
        reviewLikeDbStorage.create(userId, reviewId);
    }

    public void delete(Long userId, Long reviewId) {
        reviewLikeDbStorage.delete(userId, reviewId);
    }
}
