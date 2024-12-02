package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Data
public class Film {
    private Long id;

    @NotBlank(groups = Create.class)
    private String name;

    @Pattern(regexp = ".{0,200}", groups = {Create.class, Update.class})
    private String description;

    private LocalDate releaseDate;

    @Positive(groups = {Create.class, Update.class})
    private Integer duration;

    public interface Create {
    }

    public interface Update {
    }
}
