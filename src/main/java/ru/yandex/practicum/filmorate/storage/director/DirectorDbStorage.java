package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DirectorDbStorage {
    Optional<Director> findById(long id);

    Collection<Director> findAll();

    Director add(Director director);

    Director update(Director director);

    void delete(long id);

    Collection<Director> findAllByFilmId(long id);

    Map<Long, List<Director>> findAllByFilms();
}
