package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final ReviewLikeService reviewLikeService;
    private final ReviewDislikeService reviewDislikeService;

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

    public void setReviewLike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        userService.findById(userId);
        if (reviewLikeService.find(userId, reviewId).isPresent()) {
            throw new DataIntegrityViolationException("Этот пользователь уже поставил лайк этому отзыву");
        }
        if (reviewDislikeService.find(userId, reviewId).isPresent()) {
            reviewDislikeService.delete(userId, reviewId);
            reviewLikeService.create(userId, reviewId);
            review.setUseful(review.getUseful() + 2);
            reviewDbStorage.update(review);
            return;
        }
        reviewLikeService.create(userId, reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDbStorage.update(review);

    }

    public void setReviewDislike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        userService.findById(userId);
        if (reviewDislikeService.find(userId, reviewId).isPresent()) {
            throw new DataIntegrityViolationException("Этот пользователь уже поставил дизлайк этому отзыву");
        }
        if (reviewLikeService.find(userId, reviewId).isPresent()) {
            reviewLikeService.delete(userId, reviewId);
            reviewDislikeService.create(userId, reviewId);
            review.setUseful(review.getUseful() - 2);
            reviewDbStorage.update(review);
            return;
        }
        reviewDislikeService.create(userId, reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDbStorage.update(review);
    }

    public void removeReviewLike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        userService.findById(userId);
        if (reviewLikeService.find(userId, reviewId).isEmpty()) {
            throw new DataIntegrityViolationException("Нельзя убрать лайк, если раньше его не ставили");
        }
        reviewLikeService.delete(userId, reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDbStorage.update(review);
    }

    public void removeReviewDislike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        userService.findById(userId);
        if (reviewDislikeService.find(userId, reviewId).isEmpty()) {
            throw new DataIntegrityViolationException("Нельзя убрать дизлайк, если раньше его не ставили");
        }
        reviewDislikeService.delete(userId, reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDbStorage.update(review);
    }

}
