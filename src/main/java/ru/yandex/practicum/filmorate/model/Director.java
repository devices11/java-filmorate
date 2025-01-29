package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.validation.groups.Create;
import ru.yandex.practicum.filmorate.util.validation.groups.Update;

@Builder(toBuilder = true)
@Data
public class Director {
    @NotNull(groups = Update.class)
    private Integer id;

    @NotBlank(groups = {Create.class, Update.class})
    private String name;
}
