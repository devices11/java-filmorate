package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureDataJdbc
@ComponentScan({"ru.yandex.practicum.filmorate"})
public class UserDbStorageTests {
    private static User user;

    @Autowired
    private UserDbStorage userDbStorage;

    @BeforeAll
    static void setUp() {
        user = User.builder()
                .login("test")
                .email("test@test.com")
                .login("testlogin")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @DisplayName("Добавление пользователя")
    @Order(1)
    @Test
    public void addFilm() {
        User userFromDb = userDbStorage.create(user);

        assertThat(userFromDb)
                .isNotNull()
                .isEqualTo(user);
    }

    @DisplayName("Получение пользователя по id")
    @Test
    public void findById() {
        User userFromDb = userDbStorage.create(user);
        Optional<User> findUserFromDB = userDbStorage.findById(userFromDb.getId());

        assertThat(findUserFromDB)
                .isPresent()
                .get()
                .isEqualTo(user);
    }

    @DisplayName("Получение всех пользователей")
    @Test
    public void findAll() {
        Collection<User> usersFromDb = userDbStorage.findAll();

        assertThat(usersFromDb)
                .isNotEmpty();

        assertThat(usersFromDb.stream()
                .filter(user1 -> user1.getId().equals(user.getId()))
                .findFirst())
                .isPresent()
                .get()
                .isEqualTo(user);
    }

    @DisplayName("Обновление пользователя")
    @Test
    public void update() {
        User userFromDb = userDbStorage.create(user);
        User updateUser = User.builder()
                .id(userFromDb.getId())
                .login("test2")
                .email("test2@test.com")
                .build();

        User updateFromDB = userDbStorage.update(updateUser);

        assertThat(updateFromDB)
                .isNotNull()
                .isEqualTo(updateUser);
    }

    @DisplayName("Добавить друга и получить список друзей")
    @Test
    public void addFriend() {
        User user1 = userDbStorage.create(user);
        User user2 = userDbStorage.create(user);

        userDbStorage.addFriend(false, user1.getId(), user2.getId());

        List<User> userFriends = userDbStorage.getFriends(user1.getId());
        assertThat(userFriends.stream()
                .findFirst()
                .get()
                .getId())
                .isEqualTo(user2.getId());

    }

    @DisplayName("Обновить статус дружбы")
    @Test
    public void updateFriend() {
        User user1 = userDbStorage.create(user);
        User user2 = userDbStorage.create(user);
        userDbStorage.addFriend(false, user1.getId(), user2.getId());
        userDbStorage.updateFriend(true, user1.getId(), user2.getId());

        List<User> userFriends = userDbStorage.getFriends(user1.getId());
        assertThat(userFriends.stream()
                .findFirst()
                .get()
                .getConfirmed())
                .isEqualTo(true);

    }

    @DisplayName("Удалить из друзей")
    @Test
    public void deleteFriend() {
        User user1 = userDbStorage.create(user);
        User user2 = userDbStorage.create(user);
        userDbStorage.addFriend(false, user1.getId(), user2.getId());
        userDbStorage.deleteFriend(user1.getId(), user2.getId());

        List<User> userFriends = userDbStorage.getFriends(user1.getId());
        assertThat(userFriends).isEmpty();
    }
}
