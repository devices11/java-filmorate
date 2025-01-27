package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userStorage;

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
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
        validateUserExistence(id);
        return userStorage.getFriends(id);
    }

    public void addFriend(Long userId, Long friendId) {
        validateFriendship(userId, friendId);

        boolean isRequestPending = getFriendForUser(friendId, userId)
                .filter(friend -> !friend.getConfirmed())
                .isPresent();

        if (isRequestPending) {
            userStorage.updateFriend(true, friendId, userId);
        }

        userStorage.addFriend(isRequestPending, userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        validateUserExistence(userId, friendId);
        if (getFriendForUser(userId, friendId).isPresent()) {
            userStorage.deleteFriend(userId, friendId);
        }
    }

    public Collection<User> commonFriends(Long id, Long otherId) {
        validateUserExistence(id, otherId);
        if (id.equals(otherId)) {
            throw new ValidationException("Пользователи должны отличаться");
        }

        Set<User> friendsUser = new HashSet<>(getFriends(id));
        Set<User> friendsOtherUser = new HashSet<>(getFriends(otherId));
        friendsUser.retainAll(friendsOtherUser);

        return friendsUser;
    }

    private Optional<User> getFriendForUser(Long userId, Long friendId) {
        return getFriends(userId).stream()
                .filter(user -> user.getId().equals(friendId))
                .findFirst();
    }

    private void validateUserExistence(Long... userIds) {
        for (Long userId : userIds) {
            findById(userId);
        }
    }

    private void validateFriendship(Long userId, Long friendId) {
        validateUserExistence(userId, friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }

        Optional<User> friendUser = getFriendForUser(userId, friendId);
        if (friendUser.isPresent()) {
            if (friendUser.get().getConfirmed())
                throw new ValidationException("Пользователь уже добавлен в друзья");
            else
                throw new ValidationException("Пользователю уже направлена заявка в друзья");
        }
    }

    public void delete(long id) {
        validateUserExistence(id);
        userStorage.delete(id);
    }
}
