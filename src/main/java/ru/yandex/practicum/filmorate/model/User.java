package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
public class User {
    private Long id;

    @Email
    @NotBlank
    @NotNull
    private String email;

    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}
