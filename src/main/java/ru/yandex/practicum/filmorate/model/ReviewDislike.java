package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ReviewDislike {
    private long id;

    private Long userId;

    private Long reviewId;
}
