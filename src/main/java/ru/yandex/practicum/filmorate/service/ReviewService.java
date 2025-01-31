package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final UserService userService;
    private final FilmService filmService;

    public Review findById(Long id) {
        return reviewDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    public Review create(Review review) {
        review.setUseful(0);
        filmService.findById(review.getFilmId());
        userService.findById(review.getUserId());
        return reviewDbStorage.create(review);
    }

    public Review update(Review review) {
        Review reviewFromStorage = findById(review.getReviewId());
        reviewFromStorage.setContent(review.getContent());
        reviewFromStorage.setIsPositive(review.getIsPositive());
        return reviewDbStorage.update(reviewFromStorage);
    }

    public void delete(Long id) {
        findById(id);
        reviewDbStorage.delete(id);
    }
}
