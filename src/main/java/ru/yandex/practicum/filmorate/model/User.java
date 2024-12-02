package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Data
public class User {
    private Long id;

    @Email(groups = {User.Create.class, User.Update.class})
    @NotBlank(groups = User.Create.class)
    private String email;

    @NotBlank(groups = User.Create.class)
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", groups = {User.Create.class, User.Update.class})
    private String login;

    private String name;

    @Past(groups = {User.Create.class, User.Update.class})
    private LocalDate birthday;

    public interface Create {
    }

    public interface Update {
    }
}
