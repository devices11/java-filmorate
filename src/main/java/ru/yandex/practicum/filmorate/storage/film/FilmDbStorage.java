package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Search;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmDbStorage {

    List<Film> searchByFilmsAndDirectors(String query, List<Search> by);

    List<Film> findLikedFilmsByUserId(long userId);

    Optional<Film> findById(long id);

    Collection<Film> findAll();

    Film add(Film film);

    Film update(Film film);

    Collection<Film> findPopular(Integer count, Long genreId, Integer year);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    void delete(long id);

    void deleteAllLikeByFilmId(long filmId);

    void deleteAllLikeByUserId(long id);

    Collection<Film> findByDirectorId(int directorId, String sortBy);
}
