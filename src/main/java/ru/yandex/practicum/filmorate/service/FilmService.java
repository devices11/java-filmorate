package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    public List<Film> findFilmsByUserId(long userId, long friendId) {
        checkUser(userId);
        checkUser(friendId);
        List<Long> filmsId = filmStorage.findFilmsByUserId(userId);
        List<Long> filmsFriend = filmStorage.findFilmsByUserId(friendId);
        return filmsId.stream()
                .filter(filmsFriend::contains)
                .map(filmStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::setGenres)
                .sorted((o1, o2) -> {
                    Long likesFilm1 = filmStorage.getCountLikesFilm(o1.getId());
                    Long likesFilm2 = filmStorage.getCountLikesFilm(o2.getId());
                    if (likesFilm1 - likesFilm2 > 0) {
                        return 1;
                    }
                    if (likesFilm1 - likesFilm2 < 0) {
                        return -1;
                    }
                    return 0;
                })
                .toList();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .map(this::setGenres)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным id не найден, id=" + id));
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        Map<Long, List<Genre>> genresByFilmId = genreStorage.findAllByFilms();
        films.forEach(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), List.of())));

        return films;
    }

    public Film add(Film film) {
        validateFilm(film);
        Film filmDb = filmStorage.add(film);
        filmDb.setMpa(mpaStorage.findById(filmDb.getMpa().getId()).get());
        filmDb.setGenres(genreStorage.findAllByFilmId(filmDb.getId()));
        return filmDb;
    }

    public Film update(Film film) {
        Film updatedFilm = findById(film.getId());
        validateFilm(film);
        if (film.getName() != null) {
            if (film.getName().isBlank())
                throw new ValidationException("Название фильма не может быть пустым");
            updatedFilm.setName(film.getName());
        }
        if (film.getDescription() != null)
            updatedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) {
            updatedFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null)
            updatedFilm.setDuration(film.getDuration());
        if (film.getMpa() != null) {
            updatedFilm.setMpa(film.getMpa());
        }
        if (film.getGenres() != null) {
            updatedFilm.setGenres(film.getGenres());
        }
        Film filmDb = filmStorage.update(updatedFilm);
        filmDb.setMpa(mpaStorage.findById(filmDb.getMpa().getId()).get());
        filmDb.setGenres(genreStorage.findAllByFilmId(filmDb.getId()));
        return filmDb;
    }

    public void setLike(Long filmId, Long userId) {
        findById(filmId);
        checkUser(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        findById(filmId);
        checkUser(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> findPopular(Integer count) {
        Collection<Film> films = filmStorage.findPopular(count);
        Map<Long, List<Genre>> genresByFilmId = genreStorage.findAllByFilms();
        films.forEach(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), List.of())));

        return films;
    }

    private void checkUser(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateFilm(Film film) {
        validateMpa(film.getMpa());
        validateGenres(film.getGenres());
    }

    private void validateMpa(Mpa mpa) {
        if (mpa != null && mpaStorage.findById(mpa.getId()).isEmpty()) {
            throw new NotFoundException("Возрастной рейтинг с указанным id не найден");
        }
    }

    private void validateGenres(Collection<Genre> genres) {
        if (genres != null) {
            genres.forEach(genre -> {
                if (genreStorage.findById(genre.getId()).isEmpty()) {
                    throw new NotFoundException("Жанр с указанным id не найден");
                }
            });
        }
    }

    private Film setGenres(Film film) {
        film.setGenres(genreStorage.findAllByFilmId(film.getId()));
        return film;
    }

    public void delete(long id) {
        findById(id);
        filmStorage.deleteAllLikeByFilmId(id);
        genreStorage.deleteConnectionByFilmId(id);
        filmStorage.delete(id);
    }
}