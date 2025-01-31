package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ReviewLike {
    private long id;

    private Long userId;

    private Long reviewId;
}
