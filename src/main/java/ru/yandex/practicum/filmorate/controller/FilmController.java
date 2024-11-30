package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
    public Film add(@Valid @RequestBody Film film) {
        log.info("Получен объект для создания {}", film);
        checkFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Запись успешно создана {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен объект для обновления {}", film);
        if (!films.containsKey(film.getId()))
            throw new ValidationException("Id фильма должен быть указан, id=" + film.getId());
        checkFilm(film);
        films.put(film.getId(), film);
        log.info("Запись успешно обновлена {}", films.get(film.getId()));
        return films.get(film.getId());
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
