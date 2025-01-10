package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage storage;

    public Genre findById(Long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }

    public Collection<Genre> findAll() {
        return storage.findAll();
    }
}
