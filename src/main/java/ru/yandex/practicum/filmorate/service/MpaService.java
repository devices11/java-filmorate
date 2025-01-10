package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage storage;

    public Mpa findById(Integer id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Возрастной рейтинг с указанным id не найден"));
    }

    public Collection<Mpa> findAll() {
        return storage.findAll();
    }
}
