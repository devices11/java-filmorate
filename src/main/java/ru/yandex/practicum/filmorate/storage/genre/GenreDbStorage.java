package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreDbStorage {
    Optional<Genre> findById(long id);

    Collection<Genre> findAll();

    Collection<Genre> findAllByFilmId(long id);
}
