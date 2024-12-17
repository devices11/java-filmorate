package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film findById(Long id) {
        Optional<Film> filmFromStorage = filmStorage.findById(id);
        if (filmFromStorage.isEmpty())
            throw new NotFoundException("Фильм с указанным id не найден, id=" + id);
        return filmFromStorage.get();
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        Film updatedFilm = findById(film.getId());
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
        return filmStorage.update(updatedFilm);
    }

    public void setLike(Long id, Long userId) {
        Film filmFromStorage = findById(id);
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Set<Long> likes = new HashSet<>();
        if (filmFromStorage.getLikes() != null) {
            likes = filmFromStorage.getLikes();
        }
        likes.add(userId);
        filmFromStorage.setLikes(likes);
    }

    public void deleteLike(Long id, Long userId) {
        Film filmFromStorage = findById(id);
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Set<Long> likes = new HashSet<>();
        if (filmFromStorage.getLikes() != null) {
            likes.remove(userId);
        }
        filmFromStorage.setLikes(likes);
    }

    public Collection<Film> findPopular(Long count) {
        return filmStorage.findAll().stream()
                .filter(film -> film.getLikes() != null && film.getLikes().size() > 0)
                .sorted((new Comparator<Film>() {
                    public int compare(Film film1, Film film2) {
                        return film1.getLikes().size() - film2.getLikes().size();
                    }
                }).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
