package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmDbStorage {

    List<Integer> findFilmsByUserId(long userId);

    Optional<Film> findById(long id);

    Collection<Film> findAll();

    Film add(Film film);

    Film update(Film film);

    Collection<Film> findPopular(Integer count);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);
}
