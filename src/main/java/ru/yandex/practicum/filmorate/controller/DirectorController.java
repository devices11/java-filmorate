package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.util.validation.groups.Create;
import ru.yandex.practicum.filmorate.util.validation.groups.Update;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/{id}")
    public Director findById(@PathVariable int id) {
        return directorService.findById(id);
    }

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director add(@Validated(Create.class) @RequestBody Director director) {
        return directorService.add(director);
    }

    @PutMapping
    public Director update(@Validated(Update.class) @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        directorService.delete(id);
    }

}
