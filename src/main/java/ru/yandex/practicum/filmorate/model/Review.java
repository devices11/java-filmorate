package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.validation.groups.Create;
import ru.yandex.practicum.filmorate.util.validation.groups.Update;

@Builder(toBuilder = true)
@Data
public class Review {
    @NotNull(groups = Update.class)
    private Long reviewId;

    @NotBlank(groups = {Create.class, Update.class})
    private String content;

    @NotNull(groups = {Create.class, Update.class})
    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private Integer useful;
}
