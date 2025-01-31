package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.Collection;
import java.util.Objects;

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

    public Collection<Review> findReviews(Long filmId, int count) {
        if (Objects.nonNull(filmId)) {
            filmService.findById(filmId);
        }
        if (count <= 0) {
            throw new ValidationException("Нельзя найти 0 или меньше, чем 0, комментариев");
        }
        return reviewDbStorage.findReviews(filmId, count);
    }

    public void setLike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        //TODO: добавить логику для проверки, что пользователь еще не ставил лайк + (Если ставит лайк, до дис удалить)
        review.setUseful(review.getUseful() + 1);
        reviewDbStorage.update(review);
    }

    public void setDislike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        //TODO: добавить логику для проверки, что пользователь еще не ставил дизлайк + (Если ставит диз, то лайк удалить)
        review.setUseful(review.getUseful() - 1);
        reviewDbStorage.update(review);
    }

    public void removeLike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDbStorage.update(review);
    }

    public void removeDislike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDbStorage.update(review);
    }

}
