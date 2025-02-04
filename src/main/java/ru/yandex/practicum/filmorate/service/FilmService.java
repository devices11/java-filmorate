package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDislikeDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.model.Event.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.Event.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.Event.Operation.REMOVE;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private final DirectorDbStorage directorStorage;
    private final EventDbStorage eventStorage;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    private final ReviewDislikeDbStorage reviewDislikeDbStorage;

    public List<Film> findCommonFilms(long userId, long friendId) {
        checkUser(userId);
        checkUser(friendId);
        Collection<Film> filmsUser = setGenresAndDirectorsToFilms(filmStorage.findLikedFilmsByUserId(userId));
        Collection<Film> filmsFriend = setGenresAndDirectorsToFilms(filmStorage.findLikedFilmsByUserId(friendId));
        return filmsUser.stream()
                .filter(filmsFriend::contains)
                .toList();
    }

    public Collection<Film> searchByTitleAndDirector(String query, List<String> by) {
        if (query.isBlank()) {
            throw new ValidationException("Задан пустой поисковый запрос");
        }
        boolean paramsCheck = by.stream().limit(2)
                .allMatch(searchBy -> searchBy.equals("title") || searchBy.equals("director"));
        if (!paramsCheck) {
            throw new ValidationException("указан неправильный параметр запроса");
        }
        List<Film> films = filmStorage.searchByFilmsAndDirectors(query, by);
        return setGenresAndDirectorsToFilms(films);
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .map(this::setGenres)
                .map(this::setDirectors)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным id не найден, id=" + id));
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        return setGenresAndDirectorsToFilms(films);
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
        updatedFilm.setDirectors(film.getDirectors());
        Film filmDb = filmStorage.update(updatedFilm);
        filmDb.setMpa(mpaStorage.findById(filmDb.getMpa().getId()).get());
        filmDb.setGenres(genreStorage.findAllByFilmId(filmDb.getId()));
        filmDb.setDirectors(directorStorage.findAllByFilmId(filmDb.getId()));
        return filmDb;
    }

    public void setLike(Long filmId, Long userId) {
        findById(filmId);
        checkUser(userId);
        eventStorage.addEvent(userId.intValue(), LIKE, ADD, filmId.intValue());
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        findById(filmId);
        checkUser(userId);
        eventStorage.addEvent(userId.intValue(), LIKE, REMOVE, filmId.intValue());
        filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> findPopular(Integer count, Long genreId, Integer year) {
        Collection<Film> films = filmStorage.findPopular(count, genreId, year);
        return setGenresAndDirectorsToFilms(films);
    }

    public Collection<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        checkDirector(directorId);
        Collection<Film> films = filmStorage.findByDirectorId(directorId, sortBy);
        return setGenresAndDirectorsToFilms(films);
    }

    private void checkUser(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void checkDirector(Integer directorId) {
        if (directorStorage.findById(directorId).isEmpty()) {
            throw new NotFoundException("Режиссер не найден");
        }
    }

    public Collection<Film> setGenresAndDirectorsToFilms(Collection<Film> films) {
        Map<Long, List<Genre>> genresByFilmId = genreStorage.findAllByFilms();
        Map<Long, List<Director>> directorsByFilmId = directorStorage.findAllByFilms();
        films.forEach(film -> {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), List.of()));
            film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), List.of()));
        });

        return films;
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
        directorStorage.deleteConnectionByFilmId(id);
        reviewLikeDbStorage.deleteAllByFilmId(id);
        reviewDislikeDbStorage.deleteAllByFilmId(id);
        filmStorage.delete(id);
    }
}