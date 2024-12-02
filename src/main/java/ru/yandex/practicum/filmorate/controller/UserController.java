package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
    public User create(@Validated(User.Create.class) @RequestBody User user) {
        log.info("Получен объект для создания {}", user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank())
            user.setName(user.getLogin());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан {}", user);
        return user;
    }

    @PutMapping
    public User update(@Validated(User.Update.class) @RequestBody User user) {
        log.info("Получен объект для обновления {}", user);
        if (user.getId() == null)
            throw new ValidationException("Id должен быть указан");
        if (!users.containsKey(user.getId()))
            throw new ValidationException("Пользователь не найден");
        User updatedUser = users.get(user.getId());
        if (user.getEmail() != null) {
            if (user.getEmail().isBlank())
                throw new ValidationException("email не может быть пустым");
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            if (user.getLogin().isBlank())
                throw new ValidationException("Логин не может быть пустым");
            updatedUser.setLogin(user.getLogin());
        }
        if (user.getName() != null) {
            if (user.getName().isBlank())
                updatedUser.setName(user.getLogin());
            else updatedUser.setName(user.getName());
        }
        if (user.getBirthday() != null)
            updatedUser.setBirthday(user.getBirthday());
        users.put(user.getId(), updatedUser);
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
