package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaDbStorage {
    Optional<Mpa> findById(long id);

    Collection<Mpa> findAll();
}
