package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User findById(Long id) {
        Optional<User> user = userStorage.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user.get();
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        User userFromStorage = findById(user.getId());
        if (user.getEmail() != null) {
            if (user.getEmail().isBlank())
                throw new ValidationException("email не может быть пустым");
            userFromStorage.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            if (user.getLogin().isBlank())
                throw new ValidationException("Логин не может быть пустым");
            userFromStorage.setLogin(user.getLogin());
        }
        if (user.getName() != null) {
            if (user.getName().isBlank())
                userFromStorage.setName(user.getLogin());
            else userFromStorage.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            userFromStorage.setBirthday(user.getBirthday());
        }
        return userStorage.update(userFromStorage);
    }

    public Collection<User> getFriends(Long id) {
        User userFromStorage = findById(id);
        if (userFromStorage.getFriends() == null) {
            return new HashSet<>();
        }
        return userFromStorage.getFriends().stream()
                .map(userStorage::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public User addFriend(Long userId, Long friendId) {
        User userFromStorage = findById(userId);
        User friendFromStorage = findById(friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        updateFriends(userFromStorage, friendId);
        updateFriends(friendFromStorage, userId);
        return userFromStorage;
    }

    public void deleteFriend(Long userId, Long friendId) {
        User userFromStorage = findById(userId);
        User friendFromStorage = findById(friendId);
        deleteFriends(userFromStorage, friendId);
        deleteFriends(friendFromStorage, userId);
    }

    public Collection<User> commonFriends(Long id, Long otherId) {
        User userFirstFromStorage = findById(id);
        User userNextFromStorage = findById(otherId);
        if (id.equals(otherId)) {
            throw new ValidationException("Пользователи должны отличаться");
        }
        Set<Long> result = new HashSet<>(userFirstFromStorage.getFriends());
        result.retainAll(userNextFromStorage.getFriends());
        return result.stream()
                .map(userStorage::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private void updateFriends(User user, Long friendId) {
        Set<Long> friends = new HashSet<>();
        if (user.getFriends() != null) {
            friends = user.getFriends();
        }
        friends.add(friendId);
        user.setFriends(friends);
    }

    private void deleteFriends(User user, Long friendId) {
        Set<Long> friends;
        if (user.getFriends() != null) {
            friends = user.getFriends();
            friends.remove(friendId);
        }
    }

}
