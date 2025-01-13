package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserDbStorage {

    Optional<User> findById(long id);

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    void addFriend(boolean confirmed, long userId, long friendId);

    void updateFriend(boolean confirmed, long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getFriends(long userId);
}
