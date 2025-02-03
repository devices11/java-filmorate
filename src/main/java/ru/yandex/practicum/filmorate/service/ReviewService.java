package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewDislike;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDislikeDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikeDbStorage;
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
    private final EventService eventService;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    private final ReviewDislikeDbStorage reviewDislikeDbStorage;

    public Review findById(Long id) {
        return reviewDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    public Review create(Review review) {
        review.setUseful(0);
        filmService.findById(review.getFilmId());
        userService.findById(review.getUserId());
        Review resultReview = reviewDbStorage.create(review);
            eventService.addEvent(review.getUserId().intValue(), Event.EventType.REVIEW, Event.Operation.ADD, review.getReviewId().intValue());
        return resultReview;
    }

    public Review update(Review review) {
        Review reviewFromStorage = findById(review.getReviewId());
        reviewFromStorage.setContent(review.getContent());
        reviewFromStorage.setIsPositive(review.getIsPositive());
        Review resultReview =reviewDbStorage.update(reviewFromStorage);
        eventService.addEvent(reviewFromStorage.getUserId().intValue(), Event.EventType.REVIEW, Event.Operation.UPDATE, review.getReviewId().intValue());
        return resultReview;
    }

    public void delete(Long id) {
        findById(id);
        reviewLikeDbStorage.deleteAllByReviewId(id);
        reviewDislikeDbStorage.deleteAllByReviewId(id);
        eventService.addEvent(findById(id).getUserId().intValue(), Event.EventType.REVIEW, Event.Operation.REMOVE, findById(id).getReviewId().intValue());
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
        if (reviewLikeDbStorage.find(userId, reviewId).isPresent()) {
            throw new DataIntegrityViolationException("Этот пользователь уже поставил лайк этому отзыву");
        }
        ReviewLike reviewLike = ReviewLike.builder()
                .reviewId(reviewId)
                .userId(userId)
                .build();
        ReviewDislike reviewDislike = reviewDislikeDbStorage.find(userId, reviewId).orElse(null);
        reviewLikeDbStorage.create(reviewLike);
        if (Objects.nonNull(reviewDislike)) {
            reviewDislikeDbStorage.delete(reviewDislike.getId());
            review.setUseful(review.getUseful() + 2);
            reviewDbStorage.update(review);
            return;
        }
        review.setUseful(review.getUseful() + 1);
        reviewDbStorage.update(review);

    }

    public void setReviewDislike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        userService.findById(userId);
        if (reviewDislikeDbStorage.find(userId, reviewId).isPresent()) {
            throw new DataIntegrityViolationException("Этот пользователь уже поставил дизлайк этому отзыву");
        }
        ReviewDislike reviewDislike = ReviewDislike.builder()
                .reviewId(reviewId)
                .userId(userId)
                .build();
        ReviewLike reviewLike = reviewLikeDbStorage.find(userId, reviewId).orElse(null);
        reviewDislikeDbStorage.create(reviewDislike);
        if (Objects.nonNull(reviewLike)) {
            reviewLikeDbStorage.delete(reviewLike.getId());
            review.setUseful(review.getUseful() - 2);
            reviewDbStorage.update(review);
            return;
        }
        review.setUseful(review.getUseful() - 1);
        reviewDbStorage.update(review);
    }

    public void removeReviewLike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        userService.findById(userId);
        ReviewLike reviewLike = reviewLikeDbStorage.find(userId, reviewId).orElse(null);
        if (Objects.isNull(reviewLike)) {
            throw new DataIntegrityViolationException("Нельзя убрать лайк, если раньше его не ставили");
        }
        reviewLikeDbStorage.delete(reviewLike.getId());
        review.setUseful(review.getUseful() - 1);
        reviewDbStorage.update(review);
    }

    public void removeReviewDislike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        userService.findById(userId);
        ReviewDislike reviewDislike = reviewDislikeDbStorage.find(userId, reviewId).orElse(null);
        if (Objects.isNull(reviewDislike)) {
            throw new DataIntegrityViolationException("Нельзя убрать дизлайк, если раньше его не ставили");
        }
        reviewDislikeDbStorage.delete(reviewDislike.getId());
        review.setUseful(review.getUseful() + 1);
        reviewDbStorage.update(review);
    }
}
