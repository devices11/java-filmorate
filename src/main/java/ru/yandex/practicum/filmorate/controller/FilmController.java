package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.util.validation.groups.Create;
import ru.yandex.practicum.filmorate.util.validation.groups.Update;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.findById(id);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film add(@Validated(Create.class) @RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Validated(Update.class) @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setLike(@PathVariable long id,
                        @PathVariable long userId) {
        filmService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable long id,
                           @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopular(@RequestParam(name = "count", defaultValue = "10") int count,
                                        @RequestParam(name = "genreId", required = false) Long genreId,
                                        @RequestParam(name = "year", required = false) Integer year) {
        return filmService.findPopular(count, genreId, year);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        filmService.delete(id);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> findByDirectorId(
            @PathVariable int directorId,
            @RequestParam(name = "sortBy", required = false, defaultValue = "year") String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }
}
