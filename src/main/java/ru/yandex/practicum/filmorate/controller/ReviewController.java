package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.util.validation.groups.Create;
import ru.yandex.practicum.filmorate.util.validation.groups.Update;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review findById(@PathVariable long id) {
        return reviewService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Validated(Create.class) @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Validated(Update.class) @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        reviewService.delete(id);
    }

    @GetMapping
    public Collection<Review> findReviews(
            @RequestParam(name = "filmId", required = false) Long filmId,
            @RequestParam(name = "count", defaultValue = "10") int count) {
        return reviewService.findReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.setLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void setDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.setDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.removeDislike(id, userId);
    }
}
