package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен объект для создания {}", user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank())
            user.setName(user.getLogin());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен объект для обновления {}", user);
        if (user.getId() == null)
            throw new ValidationException("Id должен быть указан");
        if (!users.containsKey(user.getId()))
            throw new ValidationException("Пользователь не найден");
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank())
            user.setName(user.getLogin());
        users.put(user.getId(), user);
        log.info("Запись успешно обновлена {}", users.get(user.getId()));
        return users.get(user.getId());
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
