package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private final DirectorDbStorage directorStorage;

    public List<Film> findCommonFilms(long userId, long friendId) {
        checkUser(userId);
        checkUser(friendId);
        List<Film> filmsUser = filmStorage.findLikedFilmsByUserId(userId);
        List<Film> filmsFriend = filmStorage.findLikedFilmsByUserId(friendId);
        return filmsUser.stream()
                .filter(filmsFriend::contains)
                .map(this::setGenres)
                .toList();
    }

    public List<Film> searchByTitleAndDirector(String query, List<String> by) {
        if (query.isBlank()) {
            throw new ValidationException("Задан пустой поисковый запрос");
        }
        List<Search> search = by.stream().map(Search::SearchOrder).toList();
        if (search.contains(null)) {
            throw new ValidationException("некоректное имя параметра");
        }
        return filmStorage.searchByTitleAndDirector(query, search);
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .map(this::setGenres)
                .map(this::setDirectors)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным id не найден, id=" + id));
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        Map<Long, List<Genre>> genresByFilmId = genreStorage.findAllByFilms();
        Map<Long, List<Director>> directorsByFilmId = directorStorage.findAllByFilms();
        films.forEach(film -> {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), List.of()));
            film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), List.of()));
        });

        return films;
    }

    public Film add(Film film) {
        validateFilm(film);
        Film filmDb = filmStorage.add(film);
        filmDb.setMpa(mpaStorage.findById(filmDb.getMpa().getId()).get());
        filmDb.setGenres(genreStorage.findAllByFilmId(filmDb.getId()));
        filmDb.setDirectors(directorStorage.findAllByFilmId(filmDb.getId()));
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
        if (film.getDirectors() != null) {
            updatedFilm.setDirectors(film.getDirectors());
        }
        Film filmDb = filmStorage.update(updatedFilm);
        filmDb.setMpa(mpaStorage.findById(filmDb.getMpa().getId()).get());
        filmDb.setGenres(genreStorage.findAllByFilmId(filmDb.getId()));
        filmDb.setDirectors(directorStorage.findAllByFilmId(filmDb.getId()));
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

    public Collection<Film> findPopular(Integer count, Long genreId, Integer year) {
        Collection<Film> films = filmStorage.findPopular(count, genreId, year);
        Map<Long, List<Genre>> genresByFilmId = genreStorage.findAllByFilms();
        Map<Long, List<Director>> directorsByFilmId = directorStorage.findAllByFilms();
        films.forEach(film -> {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), List.of()));
            film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), List.of()));
        });

        return films;
    }

    public Collection<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        checkDirector(directorId);
        Collection<Film> films = filmStorage.findByDirectorId(directorId, sortBy);
        Map<Long, List<Genre>> genresByFilmId = genreStorage.findAllByFilms();
        Map<Long, List<Director>> directorsByFilmId = directorStorage.findAllByFilms();
        films.forEach(film -> {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), List.of()));
            film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), List.of()));
        });

        return films;
    }

    private void checkUser(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void checkDirector(Integer directorId) {
        if (directorStorage.findById(directorId).isEmpty()) {
            throw new NotFoundException("Режисер не найден");
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

    private Film setDirectors(Film film) {
        film.setDirectors(directorStorage.findAllByFilmId(film.getId()));
        return film;
    }

    public void delete(long id) {
        findById(id);
        filmStorage.deleteAllLikeByFilmId(id);
        genreStorage.deleteConnectionByFilmId(id);
        filmStorage.delete(id);
    }
}