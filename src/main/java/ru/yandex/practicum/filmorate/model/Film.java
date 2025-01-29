package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.validation.annotation.ReleaseDate;
import ru.yandex.practicum.filmorate.util.validation.groups.Create;
import ru.yandex.practicum.filmorate.util.validation.groups.Update;

import java.time.LocalDate;
import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Data
public class Film {
    @NotNull(groups = Update.class)
    private Long id;

    @NotBlank(groups = Create.class)
    private String name;

    @Pattern(regexp = ".{0,200}", groups = {Create.class, Update.class})
    private String description;

    @ReleaseDate(groups = {Create.class, Update.class})
    private LocalDate releaseDate;

    @Positive(groups = {Create.class, Update.class})
    private Integer duration;

    private Mpa mpa;

    private Collection<Genre> genres;

    private Collection<Director> directors;

}
