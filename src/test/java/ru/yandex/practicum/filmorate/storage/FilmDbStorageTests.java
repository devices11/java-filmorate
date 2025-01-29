package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureDataJdbc
@ComponentScan({"ru.yandex.practicum.filmorate"})
public class FilmDbStorageTests {
    private static Film film;
    private static User user;

    @Autowired
    private FilmDbStorageImpl filmDbStorage;

    @Autowired
    UserDbStorage userDbStorage;

    @BeforeAll
    static void setUp() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        film = Film.builder()
                .name("John Wick")
                .description("The bad guy is taking revenge for the dog")
                .releaseDate(LocalDate.parse("2014-08-20"))
                .duration(88)
                .mpa(mpa)
                .build();
        user = User.builder()
                .login("test")
                .email("test@test.com")
                .build();
    }

    @DisplayName("Добавление фильма")
    @Order(1)
    @Test
    public void addFilm() {
        Film filmFromDB = filmDbStorage.add(film);

        assertThat(filmFromDB)
                .isNotNull()
                .isEqualTo(film);
    }

    @DisplayName("Получение фильма по id")
    @Test
    public void findById() {
        Film filmFromDB = filmDbStorage.add(film);
        Optional<Film> filmsFromDB = filmDbStorage.findById(filmFromDB.getId());

        assertThat(filmsFromDB)
                .isPresent()
                .get()
                .isEqualTo(film);
    }

    @DisplayName("Получение всех фильмов")
    @Test
    public void findAll() {
        Collection<Film> filmFromDB = filmDbStorage.findAll();

        assertThat(filmFromDB)
                .isNotEmpty();

        assertThat(filmFromDB.stream()
                .filter(film1 -> film1.getId().equals(film.getId()))
                .findFirst())
                .isPresent()
                .get()
                .isEqualTo(film);
    }

    @DisplayName("Обновление фильма")
    @Test
    public void update() {
        Film filmFromDB = filmDbStorage.add(film);
        Mpa mpa = Mpa.builder()
                .id(2)
                .name("PG")
                .build();
        Film updateFilm = Film.builder()
                .id(filmFromDB.getId())
                .name("TestName")
                .description("Test description")
                .releaseDate(LocalDate.parse("2011-08-20"))
                .duration(188)
                .mpa(mpa)
                .build();

        Film updateFromDB = filmDbStorage.update(updateFilm);

        assertThat(updateFromDB)
                .isNotNull()
                .isEqualTo(updateFilm);
    }

    @DisplayName("Добавить лайк к фильму")
    @Order(2)
    @Test
    public void addLike() {
        user.setId(userDbStorage.create(user).getId());
        Film filmFromDB = filmDbStorage.add(film);
        filmDbStorage.addLike(filmFromDB.getId(), user.getId());
        boolean isHaveLike = filmDbStorage.isLikeExists(filmFromDB.getId(), user.getId());

        assertThat(isHaveLike).isEqualTo(true);
    }

    @DisplayName("Популярные фильмы")
    @Order(3)
    @Test
    public void findPopular() {
        Film filmFromDB = filmDbStorage.add(film);
        filmDbStorage.addLike(filmFromDB.getId(), user.getId());
        Collection<Film> populars2 = filmDbStorage.findPopular(1);

        assertThat(populars2.size()).isEqualTo(1);

    }

    @DisplayName("Удалить лайк к фильму")
    @Order(4)
    @Test
    public void deleteLike() {
        Film filmFromDB = filmDbStorage.add(film);
        filmDbStorage.deleteLike(filmFromDB.getId(), user.getId());
        boolean isHaveLike = filmDbStorage.isLikeExists(filmFromDB.getId(), user.getId());

        assertThat(isHaveLike).isEqualTo(false);
    }
}
