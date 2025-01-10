package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.validation.groups.Create;
import ru.yandex.practicum.filmorate.util.validation.groups.Update;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Data
public class User {
    @NotNull(groups = Update.class)
    private Long id;

    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    private String email;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", groups = {Create.class, Update.class})
    private String login;

    private String name;

    @Past(groups = {Create.class, Update.class})
    private LocalDate birthday;

    private Boolean confirmed;

}