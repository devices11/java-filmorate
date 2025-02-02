package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewDislike;
import ru.yandex.practicum.filmorate.model.ReviewLike;
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
        ReviewLike reviewLike = ReviewLike.builder()
                .reviewId(reviewId)
                .userId(userId)
                .build();
        ReviewDislike reviewDislike = reviewDislikeService.find(userId, reviewId).orElse(null);
        reviewLikeService.create(reviewLike);
        if (Objects.nonNull(reviewDislike)) {
            reviewDislikeService.delete(reviewDislike.getId());
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
        if (reviewDislikeService.find(userId, reviewId).isPresent()) {
            throw new DataIntegrityViolationException("Этот пользователь уже поставил дизлайк этому отзыву");
        }
        ReviewDislike reviewDislike = ReviewDislike.builder()
                .reviewId(reviewId)
                .userId(userId)
                .build();
        ReviewLike reviewLike = reviewLikeService.find(userId, reviewId).orElse(null);
        reviewDislikeService.create(reviewDislike);
        if (Objects.nonNull(reviewLike)) {
            reviewLikeService.delete(reviewLike.getId());
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
        ReviewLike reviewLike = reviewLikeService.find(userId, reviewId).orElse(null);
        if (Objects.isNull(reviewLike)) {
            throw new DataIntegrityViolationException("Нельзя убрать лайк, если раньше его не ставили");
        }
        reviewLikeService.delete(reviewLike.getId());
        review.setUseful(review.getUseful() - 1);
        reviewDbStorage.update(review);
    }

    public void removeReviewDislike(Long reviewId, Long userId) {
        Review review = findById(reviewId);
        userService.findById(userId);
        ReviewDislike reviewDislike = reviewDislikeService.find(userId, reviewId).orElse(null);
        if (Objects.isNull(reviewDislike)) {
            throw new DataIntegrityViolationException("Нельзя убрать дизлайк, если раньше его не ставили");
        }
        reviewDislikeService.delete(reviewDislike.getId());
        review.setUseful(review.getUseful() + 1);
        reviewDbStorage.update(review);
    }

    // TODO: доделать удаление при удалении пользователя
    public void deleteAllByUserId(Long userId) {
        // нужно дропнуть все ревью юзера и все связанные ними реакции
        // и отдельно нужно дропнуть все реакции на отзывы, которые этот юзер оставил
        //Collection<Review> reviews = findAllByUserId(userId);
    }

    // TODO: доделать удаление при удалении фильма
    public void deleteAllByFilmId(Long filmId) {
        // нужно дропнуть все отзывы фильма и связанные с ними реакции
    }
}
