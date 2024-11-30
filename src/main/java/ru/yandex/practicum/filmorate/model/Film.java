package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
public class Film {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @Pattern(regexp = ".{0,200}")
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}
