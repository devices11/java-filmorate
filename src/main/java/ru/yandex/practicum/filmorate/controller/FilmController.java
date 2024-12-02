package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate CINEMA_BIRTH_DAY = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film add(@Validated(Film.Create.class) @RequestBody Film film) {
        log.info("Получен объект для создания {}", film);
        if (film.getReleaseDate() != null)
            checkFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Запись успешно создана {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Validated(Film.Update.class) @RequestBody Film film) {
        log.info("Получен объект для обновления {}", film);
        if (!films.containsKey(film.getId()))
            throw new ValidationException("Id фильма должен быть указан, id=" + film.getId());
        Film updatedFilm = films.get(film.getId());
        if (film.getName() != null) {
            if (film.getName().isBlank())
                throw new ValidationException("Название фильма не может быть пустым");
            updatedFilm.setName(film.getName());
        }
        if (film.getDescription() != null)
            updatedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) {
            checkFilm(film);
            updatedFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null)
            updatedFilm.setDuration(film.getDuration());
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Запись успешно обновлена {}", films.get(updatedFilm.getId()));
        return films.get(updatedFilm.getId());
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkFilm(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTH_DAY))
            throw new ValidationException("Дата релиза не может быть раньше " + CINEMA_BIRTH_DAY);
    }
}
