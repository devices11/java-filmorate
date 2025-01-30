package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorStorage;

    public Director findById(Integer id) {
        return directorStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с указанным id не найден, id=" + id));
    }

    public Collection<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director add(Director director) {
        try {
            return directorStorage.add(director);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Режиссер с таким именем уже существует: " + director.getName());
        }
    }

    public Director update(Director director) {
        findById(director.getId());
        try {
            return directorStorage.update(director);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Режиссер с таким именем уже существует: " + director.getName());
        }
    }

    public void delete(Integer id) {
        directorStorage.delete(findById(id).getId());
    }
}
